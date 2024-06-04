package email.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ModelMapKey {
    LIST_EMAIL("LIST_EMAIL", "listEmails"),
    ORGINAL_EMAIL("ORGINAL_EMAIL", "orginalEmail"),
    SUMMARY_EMAIL("SUMMARY_EMAIL", "summaryEmail"),
    SUGGESTION_EMAIL("SUGGESTION", "suggestionEmail");

    private final String key;
    private final String value;
}
