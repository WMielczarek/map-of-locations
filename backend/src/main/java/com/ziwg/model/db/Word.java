package com.ziwg.model.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Word {
    private static final String BASE_FORM_OPENING_TAG = "<base>";
    private static final String BASE_FORM_CLOSING_TAG = "</base>";
    private static final String CATEGORY_OPENING_TAG = "<ann";
    private static final String CATEGORY_CLOSING_TAG = "</ann>";
    private static final String IS_LOCATION_REGEX = ".*nam_loc.*>[1-9]?";

    private String baseForm;
    private String category;

    public Word(String xmlForm) {
        this.baseForm = parseBaseForm(xmlForm);
        this.category = parseCategory(xmlForm);
    }

    @JsonIgnore
    public boolean isLocation() {
        return StringUtils.isNotBlank(baseForm) && category != null && !category.isEmpty();
    }

    @JsonIgnore
    private String parseBaseForm(String xmlForm) {
        return StringUtils.substringBetween(xmlForm, BASE_FORM_OPENING_TAG, BASE_FORM_CLOSING_TAG);
    }

    @JsonIgnore
    private String parseCategory(String xmlForm) {
        String parsedCategory = null;
        String[] xmlCategories = StringUtils.substringsBetween(xmlForm, CATEGORY_OPENING_TAG, CATEGORY_CLOSING_TAG);
        if (xmlCategories != null && xmlCategories.length > 0) {
            List<String> categories = Arrays.stream(xmlCategories)
                    .filter(cat -> cat.matches(IS_LOCATION_REGEX))
                    .map(cat -> StringUtils.substringBetween(cat, "chan=\"", "\""))
                    .collect(Collectors.toList());
            if (!categories.isEmpty()) parsedCategory = categories.get(0);
        }
        return parsedCategory;
    }
}