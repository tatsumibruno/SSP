package edu.sinclair.mygps.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import edu.sinclair.mygps.dao.SelfHelpGuideQuestionResponseDao;
import edu.sinclair.mygps.dao.SelfHelpGuideResponseDao;
import edu.sinclair.ssp.model.SelfHelpGuideQuestionResponse;
import edu.sinclair.ssp.model.SelfHelpGuideResponse;

@Service
public class EarlyAlertManager {

	// EarlyAlertReferralReasonLU
	public static final String EARLY_ALERT_REFERRAL_REASON_SELF_HELP_GUIDE_THRESHOLD_EXCEEDED = "300D68EF-38C2-4B7D-AD46-7874AA5D34AC";
	public static final String EARLY_ALERT_REFERRAL_REASON_SELF_HELP_GUIDE_CRITICAL_QUESTION = "1F5729AF-0337-4E58-A001-8A9F80DBF8AA";

	// EarlyAlertFacultySuggestionLU
	public static final String EARLY_ALERT_FACULTY_SUGGESTION_SEE_ADVISOR_OR_COACH = "B2D11151-5056-A51A-80513ACDF99FEF84";

	// Early Alert Default Campus ID
	public static final String EARLY_ALERT_DEFAULT_CAMPUS_ID = "1";

	@Autowired
	private SelfHelpGuideQuestionResponseDao selfHelpGuideQuestionResponseDao;

	@Autowired
	private SelfHelpGuideResponseDao selfHelpGuideResponseDao;

	private Logger logger = LoggerFactory.getLogger(EarlyAlertManager.class);

	@Value("#{configProperties.earlyAlertApiBaseUrl}")
	private String earlyAlertApiBaseUrl;

	public void setEarlyAlertApiBaseUrl(String earlyAlertApiBaseUrl) {
		this.earlyAlertApiBaseUrl = earlyAlertApiBaseUrl;
	}

	@Transactional(readOnly = false)
	public void generateCriticalAlerts() {

		logger.info("BEGIN : generateCriticalAlerts()");

		List<SelfHelpGuideQuestionResponse> selfHelpGuideQuestionResponses = selfHelpGuideQuestionResponseDao
				.selectCriticalResponsesForEarlyAlert();

		for (SelfHelpGuideQuestionResponse selfHelpGuideQuestionResponse : selfHelpGuideQuestionResponses) {

			RestTemplate restTemplate = new RestTemplate();

			Map<String, Object> params = new HashMap<String, Object>();

			params.put("studentId", selfHelpGuideQuestionResponse
					.getSelfHelpGuideResponse().getPerson().getUserId());
			params.put("campusId", EARLY_ALERT_DEFAULT_CAMPUS_ID);
			params.put(
					"referralReason",
					EARLY_ALERT_REFERRAL_REASON_SELF_HELP_GUIDE_CRITICAL_QUESTION);
			params.put(
					"facultySuggestions",
					new String[] { EARLY_ALERT_FACULTY_SUGGESTION_SEE_ADVISOR_OR_COACH });
			params.put("comment",
					"The following critical question was answered affirmatively: "
							+ selfHelpGuideQuestionResponse
									.getSelfHelpGuideQuestion().getChallenge()
									.getSelfHelpGuideQuestion());

			try {
				logger.info("Sending Alert for student "
						+ selfHelpGuideQuestionResponse
								.getSelfHelpGuideResponse().getPerson()
								.getUserId() + " : generateCriticalAlerts()");

				String result = restTemplate.postForObject(earlyAlertApiBaseUrl
						+ "/createEarlyAlert", params, String.class);

				if (Boolean.parseBoolean(result.trim())) {
					selfHelpGuideQuestionResponse.setEarlyAlertSent(true);
					selfHelpGuideQuestionResponseDao
							.save(selfHelpGuideQuestionResponse);

					logger.info("Alert for selfHelpGuideQuestionResponse "
							+ selfHelpGuideQuestionResponse.getId()
							+ " sent successfully.");
				} else {
					logger.error("ERROR : generateCriticalAlerts() : {}",
							"Return value false from post for student self help guide question response "
									+ selfHelpGuideQuestionResponse.getId());
				}

			} catch (Exception e) {
				logger.error("ERROR : generateCriticalAlerts() : {}",
						e.getMessage(), e);
			}

		}

		logger.info("END : generateCriticalAlerts()");
	}

	public void generateThresholdAlerts() {

		logger.info("BEGIN : generateThresholdAlerts()");

		List<SelfHelpGuideResponse> selfHelpGuideResponses = selfHelpGuideResponseDao
				.selectForEarlyAlert();

		for (SelfHelpGuideResponse selfHelpGuideResponse : selfHelpGuideResponses) {

			RestTemplate restTemplate = new RestTemplate();

			Map<String, Object> params = new HashMap<String, Object>();

			params.put("studentId", selfHelpGuideResponse.getPerson()
					.getUserId());
			params.put("campusId", EARLY_ALERT_DEFAULT_CAMPUS_ID);
			params.put(
					"referralReason",
					EARLY_ALERT_REFERRAL_REASON_SELF_HELP_GUIDE_THRESHOLD_EXCEEDED);
			params.put(
					"facultySuggestions",
					new String[] { EARLY_ALERT_FACULTY_SUGGESTION_SEE_ADVISOR_OR_COACH });
			params.put("comment", "The threshold for the self help guide "
					+ selfHelpGuideResponse.getSelfHelpGuide().getName()
					+ " was exceeded.");

			try {
				logger.info("Sending Alert for student "
						+ selfHelpGuideResponse.getPerson().getUserId()
						+ " : generateThresholdAlerts()");

				String result = restTemplate.postForObject(earlyAlertApiBaseUrl
						+ "/createEarlyAlert", params, String.class);

				if (Boolean.parseBoolean(result.trim())) {
					selfHelpGuideResponse.setEarlyAlertSent(true);
					selfHelpGuideResponseDao.save(selfHelpGuideResponse);

					logger.info("Alert for selfhelpguideresponse "
							+ selfHelpGuideResponse.getId()
							+ " sent successfully.");
				} else {
					logger.error("ERROR : generateThresholdAlerts() : {}",
							"Return value false from post for student self help guide response "
									+ selfHelpGuideResponse.getId());
				}

			} catch (Exception e) {
				logger.error("ERROR : generateThresholdAlerts() : {}",
						e.getMessage(), e);
			}

		}

		logger.info("END : generateThresholdAlerts()");
	}

}
