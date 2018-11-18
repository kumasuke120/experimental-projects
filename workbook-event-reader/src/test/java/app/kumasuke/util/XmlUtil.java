package app.kumasuke.util;

import nu.xom.*;

import java.io.IOException;
import java.util.*;

public class XmlUtil {
    private XmlUtil() {
        throw new UnsupportedOperationException();
    }

    public static boolean isSameXml(String aXml, String bXml) {
        if (Objects.equals(aXml, bXml)) {
            return true;
        } else {
            final Document aDoc = buildSilently(aXml);
            final Document bDoc = buildSilently(bXml);

            final Element aRoot = aDoc.getRootElement();
            final Element bRoot = bDoc.getRootElement();

            return isSameElement(aRoot, bRoot);
        }
    }

    private static boolean isSameElement(Element aElement, Element bElement) {
        final String aQN = aElement.getQualifiedName();
        final String bQN = bElement.getQualifiedName();

        if (!Objects.equals(aQN, bQN)) {
            return false;
        } else {
            final int aAttrCount = aElement.getAttributeCount();
            final int bAttrCount = bElement.getAttributeCount();

            if (aAttrCount != bAttrCount) {
                return false;
            } else {
                final Map<String, String> aAttrs = extractAttributes(aElement);
                final Map<String, String> bAttrs = extractAttributes(bElement);

                if (!Objects.equals(aAttrs, bAttrs)) {
                    return false;
                }

                final int aChildCount = aElement.getChildCount();
                final int bChildCount = aElement.getChildCount();

                if (aChildCount != bChildCount) {
                    return false;
                } else if (aChildCount == 0) {
                    return Objects.equals(aElement.getValue(), bElement.getValue());
                } else {
                    final Map<String, List<Element>> aChildElements = extractChildElements(aElement);
                    final Map<String, List<Element>> bChildElements = extractChildElements(bElement);

                    if (aChildElements.size() != bChildElements.size()) {
                        return false;
                    }

                    for (final String name : aChildElements.keySet()) {
                        final List<Element> aElements = aChildElements.get(name);
                        final List<Element> bElements = bChildElements.get(name);

                        if (bElements == null || aElements.size() != bElements.size()) {
                            return false;
                        }

                        for (int i = 0; i < aElements.size(); i++) {
                            final Element aChildElement = aElements.get(i);
                            final Element bChildElement = bElements.get(i);

                            if (!isSameElement(aChildElement, bChildElement)) {
                                return false;
                            }
                        }
                    }
                }

                return true;
            }
        }
    }

    private static Map<String, String> extractAttributes(Element element) {
        final Map<String, String> result = new HashMap<>();

        for (int i = 0; i < element.getAttributeCount(); i++) {
            final Attribute attr = element.getAttribute(i);
            result.put(attr.getQualifiedName(), attr.getValue());
        }

        return result;
    }

    private static Map<String, List<Element>> extractChildElements(Element element) {
        final Map<String, List<Element>> result = new HashMap<>();

        final Elements childElements = element.getChildElements();
        for (int i = 0; i < childElements.size(); i++) {
            final Element e = childElements.get(i);
            final String name = e.getQualifiedName();
            final List<Element> elements = result.computeIfAbsent(name, k -> new ArrayList<>());
            elements.add(e);
        }

        return result;
    }

    private static Document buildSilently(String xml) {
        final var builder = new Builder();
        try {
            return builder.build(xml, null);
        } catch (ParsingException | IOException e) {
            throw new AssertionError(e);
        }
    }
}
