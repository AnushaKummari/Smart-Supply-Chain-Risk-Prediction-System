package com.ssrp.services;

import com.ssrp.entities.RiskLevel;
import org.springframework.stereotype.Component;

@Component
public class RiskScoreEngine {

    public Double calculateRiskScore(Double delayProbability, Double predictedDelayHours) {
        double probability = delayProbability != null ? delayProbability : 0.0;
        double delayHours = predictedDelayHours != null ? predictedDelayHours : 0.0;

        double probabilityScore = probability * 70.0;
        double normalizedDelay = Math.min(delayHours / 24.0, 1.0);
        double delayScore = normalizedDelay * 30.0;

        double score = probabilityScore + delayScore;

        if (score < 0) score = 0;
        if (score > 100) score = 100;

        return Math.round(score * 100.0) / 100.0;
    }

    public RiskLevel deriveRiskLevel(Double riskScore) {
        double score = riskScore != null ? riskScore : 0.0;

        if (score <= 30) {
            return RiskLevel.LOW;
        } else if (score <= 70) {
            return RiskLevel.MEDIUM;
        } else {
            return RiskLevel.HIGH;
        }
    }
}