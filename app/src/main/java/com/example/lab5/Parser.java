package com.example.lab5;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Parser {

    private static final String TAG = "Parser";

    public static List<CurrencyRate> parseEcbXml(InputStream inputStream) throws Exception {
        System.out.println("Parser.parseEcbXml() called");
        Log.d(TAG, "parseEcbXml() called");

        List<CurrencyRate> result = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.parse(inputStream);
        document.getDocumentElement().normalize();

        NodeList cubeNodes = document.getElementsByTagName("Cube");

        for (int i = 0; i < cubeNodes.getLength(); i++) {
            Node node = cubeNodes.item(i);
            NamedNodeMap attrs = node.getAttributes();
            if (attrs == null) {
                continue;
            }

            Node currencyAttr = attrs.getNamedItem("currency");
            Node rateAttr = attrs.getNamedItem("rate");

            if (currencyAttr != null && rateAttr != null) {
                String code = currencyAttr.getNodeValue();
                double rate = Double.parseDouble(rateAttr.getNodeValue());
                CurrencyRate currencyRate = new CurrencyRate(code, rate);
                result.add(currencyRate);
            }
        }

        Log.d(TAG, "parseEcbXml() finished, count = " + result.size());
        System.out.println("Parser.parseEcbXml() finished, count = " + result.size());
        return result;
    }
}