package no.kommune.bergen.soa.svarut;

import no.kommune.bergen.soa.common.exception.UserException;
import no.kommune.bergen.soa.svarut.dispatchers.*;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import no.kommune.bergen.soa.svarut.dto.ShipmentPolicy;

import java.util.*;

import static no.kommune.bergen.soa.svarut.dto.ShipmentPolicy.*;

/**
 * The correct dispatcher to use is determined by ShipmentPolicy
 */
public class DispatcherFactory {

	private final ServiceContext serviceContext;
	private final ServiceDelegate serviceDelegate;
	private final DispatchRateConfig rateConfig;

	private final List<Dispatcher> dispatchers = new ArrayList<Dispatcher>();

	public DispatcherFactory(ServiceDelegate serviceDelegate, ServiceContext serviceContext, DispatchRateConfig rateConfig) {

		this.serviceContext = serviceContext;
		this.serviceDelegate = serviceDelegate;
		this.rateConfig = rateConfig;

		setupDispatchersForEmail();
		setupDispatchersForPost();
		setupDispatchersForAltinn();

	}

	public List<Dispatcher> getAllDispatchers() {
		return dispatchers;
	}

	private void setupDispatchersForPost() {

		final List<ShipmentPolicy> shipmentPolicies = Arrays.asList(KUN_APOST, KUN_BPOST, KUN_REKOMMANDERT);
		List<DispatchPolicyShipmentParams> dpsp = new ArrayList<DispatchPolicyShipmentParams>();
		for (ShipmentPolicy sp : shipmentPolicies) {
			dpsp.add(new DispatchPolicyShipmentParams(sp, 1));
		}
		DispatchPolicy dispatchPolicy = new DispatchPolicy();
		dispatchPolicy.setShipmentParams(dpsp);
		dispatchPolicy.setMaxDispatchRate(rateConfig.getPost());


		dispatchPolicy.addDispatchWindow(serviceContext.getPrintContext().getDispatchWindow());
		dispatchPolicy.setPrintWindowAgeInDays(serviceContext.getPrintContext().getPrintWindowAgeInDays());

		KunPost kunPost = new KunPost(
				serviceDelegate,
				serviceContext.getForsendelsesArkiv(),
				serviceContext.getPrintFacade(),
				dispatchPolicy);

		dispatchers.add(kunPost);
	}

	private void setupDispatchersForEmail() {

		List<DispatchPolicyShipmentParams> paramsEmailOgPost = new ArrayList<DispatchPolicyShipmentParams>();
		paramsEmailOgPost.add(new DispatchPolicyShipmentParams(EMAIL_OG_APOST, serviceContext.getEmailContext().getLeadTimeApost()));
		paramsEmailOgPost.add(new DispatchPolicyShipmentParams(EMAIL_OG_BPOST, serviceContext.getEmailContext().getLeadTimeBpost()));
		paramsEmailOgPost.add(new DispatchPolicyShipmentParams(EMAIL_OG_REKOMMANDERT, serviceContext.getEmailContext().getLeadTimeRekommandert()));

		DispatchPolicy dispatchPolicyEmailOgPost = new DispatchPolicy();
		dispatchPolicyEmailOgPost.setShipmentParams(paramsEmailOgPost);
		dispatchPolicyEmailOgPost.setMaxDispatchRate(rateConfig.getEpost());
		dispatchPolicyEmailOgPost.setPrintWindowAgeInDays(serviceContext.getPrintContext().getPrintWindowAgeInDays());
		dispatchPolicyEmailOgPost.addDispatchWindow(serviceContext.getEmailContext().getDispatchWindow());

		dispatchers.add(new EmailOgPost(
				serviceDelegate,
				serviceContext.getForsendelsesArkiv(),
				serviceContext.getEmailFacade(),
				serviceContext.getPrintFacade(),
				dispatchPolicyEmailOgPost));

		List<DispatchPolicyShipmentParams> paramsKunEmail = new ArrayList<DispatchPolicyShipmentParams>();
		paramsKunEmail.add(new DispatchPolicyShipmentParams(KUN_EMAIL, 0));
		paramsKunEmail.add(new DispatchPolicyShipmentParams(KUN_EMAIL_INGEN_VEDLEGG, 0));
		DispatchPolicy dispatchPolicyKunEpost = new DispatchPolicy();
		dispatchPolicyKunEpost.setMaxDispatchRate(rateConfig.getEpost());
		dispatchPolicyKunEpost.setShipmentParams(paramsKunEmail);
		dispatchPolicyKunEpost.setPrintWindowAgeInDays(serviceContext.getPrintContext().getPrintWindowAgeInDays());
		dispatchPolicyKunEpost.addDispatchWindow(serviceContext.getEmailContext().getDispatchWindow());

		dispatchers.add(new KunEmail(
				serviceDelegate,
				serviceContext.getForsendelsesArkiv(),
				serviceContext.getEmailFacade(),
				dispatchPolicyKunEpost));

	}

	private void setupDispatchersForAltinn() {

		List<DispatchPolicyShipmentParams> paramsKunAltinn = new ArrayList<DispatchPolicyShipmentParams>();
		paramsKunAltinn.add(new DispatchPolicyShipmentParams(KUN_ALTINN, 0));
		DispatchPolicy dispatchPolicyKunAltinn = new DispatchPolicy();
		dispatchPolicyKunAltinn.setMaxDispatchRate(rateConfig.getAltinn());
		dispatchPolicyKunAltinn.setShipmentParams(paramsKunAltinn);
		dispatchPolicyKunAltinn.setPrintWindowAgeInDays(serviceContext.getPrintContext().getPrintWindowAgeInDays());
		dispatchPolicyKunAltinn.addDispatchWindow(serviceContext.getAltinnContext().getDispatchWindow());

		dispatchers.add(new KunAltinn(
				serviceDelegate,
				serviceContext.getForsendelsesArkiv(),
				serviceContext.getAltinnFacade(),
				dispatchPolicyKunAltinn));

		List<DispatchPolicyShipmentParams> paramsAltinnOgEpost = new ArrayList<DispatchPolicyShipmentParams>();
		paramsAltinnOgEpost.add(new DispatchPolicyShipmentParams(ALTINN_OG_APOST, serviceContext.getAltinnContext().getLeadTimeApost()));
		paramsAltinnOgEpost.add(new DispatchPolicyShipmentParams(ALTINN_OG_BPOST, serviceContext.getAltinnContext().getLeadTimeBpost()));
		paramsAltinnOgEpost.add(new DispatchPolicyShipmentParams(ALTINN_OG_REKOMMANDERT, serviceContext.getAltinnContext().getLeadTimeRekommandert()));

		DispatchPolicy dispatchPolicyAltinnOgPost = new DispatchPolicy();
		dispatchPolicyAltinnOgPost.setMaxDispatchRate(rateConfig.getAltinn());
		dispatchPolicyAltinnOgPost.setShipmentParams(paramsAltinnOgEpost);
		dispatchPolicyAltinnOgPost.setPrintWindowAgeInDays(serviceContext.getPrintContext().getPrintWindowAgeInDays());
		dispatchPolicyAltinnOgPost.addDispatchWindow(serviceContext.getAltinnContext().getDispatchWindow());

		dispatchers.add(new AltinnOgPost(
				serviceDelegate,
				serviceContext.getForsendelsesArkiv(),
				serviceContext.getAltinnFacade(),
				serviceContext.getPrintFacade(),
				dispatchPolicyAltinnOgPost));

	}

	/**
	 * Lokaliserer korrekt Dispatcher for forsendelsen
	 */
	public Dispatcher getDispatcher(Forsendelse forsendelse) {
		ShipmentPolicy sp = ShipmentPolicy.fromValue(forsendelse.getShipmentPolicy());
		for (Dispatcher dispatcher : dispatchers) {
			AbstractDispatcher ad = (AbstractDispatcher) dispatcher;
			if (ad.handlesShipmentPolicy(sp)) {
				ad.verify(forsendelse);
				return ad;
			}
		}
		throw new UserException("No dispatcher found for the given shipmentpolicy : " + sp.value());
	}
}
