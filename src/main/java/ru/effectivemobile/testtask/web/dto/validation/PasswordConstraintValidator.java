package ru.effectivemobile.testtask.web.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.*;

import java.util.Arrays;
import java.util.List;

/**
 * Набор правил для валидации пароля.
 */
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {
    @Override
    public void initialize(ValidPassword arg0) {
    }

    /**
     * Метод содержит массив ограничений, которые мы хотим применить в нашем пароле.
     *
     * @param password пароль который необходимо проверить.
     * @param context  контекст средства проверки ограничений.
     * @return true or false.
     */
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {

        PasswordValidator validator = new PasswordValidator(Arrays.asList(
                // не менее 8 символов
                new LengthRule(8, 30),

                // по крайней мере, один символ в верхнем регистре
                new CharacterRule(EnglishCharacterData.UpperCase, 1),

                // at least one lower-case character
                new CharacterRule(EnglishCharacterData.LowerCase, 1),

                // по крайней мере, один строчный символ
                new CharacterRule(EnglishCharacterData.Digit, 1),

                // хотя бы один символ (специальный символ)
                new CharacterRule(EnglishCharacterData.Special, 1),

                // без пробелов
                new WhitespaceRule()

        ));
        RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }

        // Если какие-то условия не выполняются, мы объединяем все сообщения об ошибках условия failed в строку,
        // разделенную символом ",", а затем помещаем ее в context и возвращаем false.
        List<String> messages = validator.getMessages(result);

        String messageTemplate = String.join(",", messages);
        context.buildConstraintViolationWithTemplate(messageTemplate)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();

        return false;
    }
}
