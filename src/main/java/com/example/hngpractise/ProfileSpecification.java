package com.example.hngpractise;

import org.springframework.data.jpa.domain.Specification;

public class ProfileSpecification {

    public static Specification<Profile> filter(
            String gender,
            String ageGroup,
            String countryId,
            Integer minAge,
            Integer maxAge,
            Double minGenderProb,
            Double minCountryProb
    ) {
        return (root, query, cb) -> {

            var predicates = cb.conjunction();

            if (gender != null) {
                predicates = cb.and(predicates,
                        cb.equal(cb.lower(root.get("gender")), gender.toLowerCase()));
            }

            if (ageGroup != null) {
                predicates = cb.and(predicates,
                        cb.equal(cb.lower(root.get("ageGroup")), ageGroup.toLowerCase()));
            }

            if (countryId != null) {
                predicates = cb.and(predicates,
                        cb.equal(cb.lower(root.get("countryId")), countryId.toLowerCase()));
            }

            if (minAge != null) {
                predicates = cb.and(predicates,
                        cb.greaterThanOrEqualTo(root.get("age"), minAge));
            }

            if (maxAge != null) {
                predicates = cb.and(predicates,
                        cb.lessThanOrEqualTo(root.get("age"), maxAge));
            }

            if (minGenderProb != null) {
                predicates = cb.and(predicates,
                        cb.greaterThanOrEqualTo(root.get("genderProbability"), minGenderProb));
            }

            if (minCountryProb != null) {
                predicates = cb.and(predicates,
                        cb.greaterThanOrEqualTo(root.get("countryProbability"), minCountryProb));
            }

            return predicates;
        };
    }
}