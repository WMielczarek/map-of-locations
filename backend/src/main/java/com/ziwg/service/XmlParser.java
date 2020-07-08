package com.ziwg.service;

import com.ziwg.model.db.Section;
import com.ziwg.model.db.Word;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class XmlParser {

    private XmlParser() {}

    public static final String XML_WORD_DESCRIPTION_REGEX = "<\\/tok>[\\s\\S]*?<tok>|<tok>|<\\/tok>";

    public static Section convertXmlToSectionObject(String xmlSection) {
        List<Word> words = getXmlWordsDescriptions(xmlSection).stream()
                .map(Word::new)
                .collect(Collectors.toList());
        return new Section(words);
    }

    private static List<String> getXmlWordsDescriptions(String xmlSection) {
        List<String> words = Arrays.asList(xmlSection.split(XML_WORD_DESCRIPTION_REGEX));
        try {
            int wordsLastIndex = words.size() - 1;
            return words.subList(1, wordsLastIndex);
        } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            log.error(e);
            return Collections.emptyList();
        }
    }
}
