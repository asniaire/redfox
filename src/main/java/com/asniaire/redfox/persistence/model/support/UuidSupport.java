package com.asniaire.redfox.persistence.model.support;

import lombok.extern.slf4j.Slf4j;

import javax.validation.*;
import java.lang.annotation.*;
import java.util.UUID;

@Slf4j
public class UuidSupport {

    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = UuidValidator.class)
    @ReportAsSingleViolation
    @Documented
    public @interface CheckedUuid {

        String message() default "{com.asniaire.imagefinder.model.checkeduuid}";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};

    }

    public static boolean validate(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (NullPointerException | IllegalArgumentException ex) {
            log.debug("Not valid uuid {}", uuid, ex);
            return false;
        }
    }

    public static class UuidValidator implements ConstraintValidator<CheckedUuid, String> {

        public boolean isValid(String value, ConstraintValidatorContext context) {
            return UuidSupport.validate(value);
        }
    }

}
