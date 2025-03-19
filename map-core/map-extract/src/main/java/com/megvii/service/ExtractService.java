package com.megvii.service;

import org.dom4j.DocumentException;

import java.io.IOException;

public interface ExtractService {
    void saveXmlFromFile(String xml, String remark, String ossPath) throws DocumentException, IOException;

}
