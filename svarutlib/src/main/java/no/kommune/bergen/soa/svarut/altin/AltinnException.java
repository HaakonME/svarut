package no.kommune.bergen.soa.svarut.altin;

import no.altinn.services.serviceengine.correspondence._2009._10.ICorrespondenceAgencyExternalBasicInsertCorrespondenceBasicV2AltinnFaultFaultFaultMessage;

public class AltinnException extends RuntimeException{
	public AltinnException(String altinFaultMessage, ICorrespondenceAgencyExternalBasicInsertCorrespondenceBasicV2AltinnFaultFaultFaultMessage e) {
		super(altinFaultMessage, e);
	}
}
