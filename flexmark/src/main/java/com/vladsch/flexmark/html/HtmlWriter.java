package com.vladsch.flexmark.html;

import com.vladsch.flexmark.html.renderer.AttributablePart;
import com.vladsch.flexmark.html.renderer.LinkStatus;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.ResolvedLink;
import com.vladsch.flexmark.util.Escaping;
import com.vladsch.flexmark.util.options.Attribute;
import com.vladsch.flexmark.util.options.Attributes;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.flexmark.util.sequence.TagRange;

import java.io.IOException;
import java.util.ArrayList;

public class HtmlWriter {
    private final Appendable buffer;
    private final int indentSize;
    private final String indentSizePrefix;
    private NodeRendererContext context;

    private char lastChar = 0;
    private int indent;
    private String indentPrefix = "";
    private Attributes currentAttributes;
    //private int appendCount = 0;
    private boolean delayedIndent = false;
    private boolean delayedEOL = false;
    private boolean indentIndentingChildren = false;
    private boolean lineOnChildText = false;
    private int preNesting = 0;
    private AttributablePart useAttributes = null;
    private int appendCount = 0;
    private String prefix = "";

    public HtmlWriter(Appendable out) {
        this(out, 0);
    }

    public HtmlWriter(HtmlWriter other, Appendable out, boolean inheritIndent) {
        this(out, other.indentSize);

        if (inheritIndent) {
            indent = other.indent;
            indentPrefix = other.indentPrefix;
        }
    }

    public HtmlWriter(Appendable out, int indentSize) {
        this.buffer = out;
        this.indentSize = indentSize;

        StringBuilder sb = new StringBuilder(indentSize);
        for (int i = 0; i < indentSize; i++) sb.append(' ');
        indentSizePrefix = sb.toString();
    }

    public String getPrefix() {
        return prefix;
    }

    public HtmlWriter setPrefix(final String prefix) {
        this.prefix = prefix;
        return this;
    }

    public int getAppendCount() {
        return appendCount;
    }

    boolean inPre() {
        return preNesting > 0;
    }

    void setContext(NodeRendererContext context) {
        this.context = context;
    }

    public NodeRendererContext getContext() {
        return context;
    }

    public int getIndentSize() {
        return indentSize;
    }

    public HtmlWriter raw(String s) {
        append(s);
        return this;
    }

    public HtmlWriter text(String text) {
        append(Escaping.escapeHtml(text, false));
        return this;
    }

    public HtmlWriter attr(String name, String value) {
        if (currentAttributes == null) {
            currentAttributes = new Attributes();
        }
        currentAttributes.replaceValue(name, value);
        return this;
    }

    public HtmlWriter srcPos() {
        return srcPos(context.getCurrentNode().getChars());
    }

    public HtmlWriter srcPosWithEOL() {
        return srcPosWithEOL(context.getCurrentNode().getChars());
    }

    public HtmlWriter srcPosWithTrailingEOL() {
        return srcPosWithTrailingEOL(context.getCurrentNode().getChars());
    }

    public HtmlWriter srcPos(BasedSequence sourceText) {
        if (sourceText.isNotNull()) {
            BasedSequence trimmed = sourceText.trimEOL();
            return srcPos(trimmed.getStartOffset(), trimmed.getEndOffset());
        }
        return this;
    }

    public HtmlWriter srcPosWithEOL(BasedSequence sourceText) {
        if (sourceText.isNotNull()) {
            return srcPos(sourceText.getStartOffset(), sourceText.getEndOffset());
        }
        return this;
    }

    public HtmlWriter srcPosWithTrailingEOL(BasedSequence sourceText) {
        if (sourceText.isNotNull()) {
            int endOffset = sourceText.getEndOffset();
            CharSequence base = sourceText.getBase();
            while (endOffset < base.length()) {
                char c = base.charAt(endOffset);
                if (c != ' ' && c != '\t') break;
                endOffset++;
            }

            if (endOffset < base.length() && base.charAt(endOffset) == '\r') {
                endOffset++;
            }

            if (endOffset < base.length() && base.charAt(endOffset) == '\n') {
                endOffset++;
            }
            return srcPos(sourceText.getStartOffset(), endOffset);
        }
        return this;
    }

    public HtmlWriter srcPos(int startOffset, int endOffset) {
        if (startOffset <= endOffset && !context.getHtmlOptions().sourcePositionAttribute.isEmpty()) {
            if (currentAttributes == null) {
                currentAttributes = new Attributes();
            }
            currentAttributes.replaceValue(context.getHtmlOptions().sourcePositionAttribute, startOffset + "-" + endOffset);
        }
        return this;
    }

    public HtmlWriter attr(Attribute attribute) {
        if (currentAttributes == null) {
            currentAttributes = new Attributes();
        }
        currentAttributes.replaceValue(attribute.getName(), attribute.getValue());
        return this;
    }

    public HtmlWriter attr(Attributes attributes) {
        if (!attributes.isEmpty()) {
            if (currentAttributes == null) {
                currentAttributes = new Attributes(attributes);
            } else {
                currentAttributes.replaceValues(attributes);
            }
        }
        return this;
    }

    public HtmlWriter tag(String name) {
        return tag(name, false, false);
    }

    public HtmlWriter tagVoid(String name) {
        return tag(name, true, false);
    }

    public HtmlWriter tagVoidLine(String name) {
        return tag(name, true, true);
    }

    public HtmlWriter withAttr() {
        return withAttr(AttributablePart.NODE);
    }

    public HtmlWriter withAttr(AttributablePart part) {
        useAttributes = part;
        return this;
    }

    public HtmlWriter withAttr(LinkStatus status) {
        attr(Attribute.LINK_STATUS, status.getName());
        return withAttr(AttributablePart.LINK);
    }

    public HtmlWriter withAttr(ResolvedLink resolvedLink) {
        return withAttr(resolvedLink.getStatus());
    }

    public HtmlWriter tag(String name, boolean voidElement, boolean voidWithLine) {
        Attributes attributes = null;

        if (useAttributes != null) {
            attributes = context.extendRenderingNodeAttributes(useAttributes, currentAttributes);
            currentAttributes = null;
            useAttributes = null;
        }

        if (voidElement && voidWithLine) line();

        append("<");
        append(name);

        if (attributes != null && !attributes.isEmpty()) {
            String sourcePositionAttribute = context.getHtmlOptions().sourcePositionAttribute;

            for (Attribute attribute : attributes.values()) {
                String attributeValue = attribute.getValue();

                if (!sourcePositionAttribute.isEmpty() && attribute.getName().equals(sourcePositionAttribute)) {
                    int pos = attributeValue.indexOf('-');
                    int startOffset = -1;
                    int endOffset = -1;

                    if (pos != -1) {
                        try {
                            startOffset = Integer.valueOf(attributeValue.substring(0, pos));
                        } catch (Throwable ignored) {

                        }
                        try {
                            endOffset = Integer.valueOf(attributeValue.substring(pos + 1));
                        } catch (Throwable ignored) {

                        }
                    }

                    if (startOffset >= 0 && startOffset < endOffset) {
                        ArrayList<TagRange> tagRanges = context.getDocument().get(HtmlRenderer.TAG_RANGES);
                        tagRanges.add(new TagRange(name, startOffset, endOffset));
                    }
                }

                if (attribute.isNonRendering()) continue;

                append(" ");
                append(Escaping.escapeHtml(attribute.getName(), true));
                append("=\"");
                append(Escaping.escapeHtml(attributeValue, true));
                append("\"");
            }
        }

        if (voidElement) {
            append(" />");
            if (voidWithLine) line();
        } else {
            append(">");
        }

        return this;
    }

    public HtmlWriter tag(String name, Runnable runnable) {
        return tag(name, false, false, runnable);
    }

    public HtmlWriter tagIndent(String name, Runnable runnable) {
        return tag(name, !indentIndentingChildren, false, runnable);
    }

    public HtmlWriter tagLine(String name, Runnable runnable) {
        return tag(name, false, !lineOnChildText, runnable);
    }

    public HtmlWriter tag(String name, boolean indentTag, boolean withLine, Runnable runnable) {
        int indentLevel = indent;
        int preIndentLevel = indent;

        boolean delayedIndent = this.delayedIndent;
        this.delayedIndent = false;

        if (delayedIndent) {
            if (indentTag) {
                indent();
                preIndentLevel = indent;
            }
        }

        if (withLine || indentTag) line();
        tag(name, false, false);

        if (lineOnChildText) {
            delayedEOL = true;
            lineOnChildText = false;
        }

        if (indentTag) indent();

        if (indentIndentingChildren) {
            this.delayedIndent = true;
            indentIndentingChildren = false;
        }

        runnable.run();

        // if not used then not needed
        this.delayedIndent = false;

        boolean hadPreIndent = preIndentLevel < indent;
        if (hadPreIndent) {
            while (preIndentLevel < indent) unIndent();
        }

        // if not used then not needed
        this.delayedEOL = false;

        append("</");
        append(name);
        append(">");

        boolean hadIndent = indentLevel < indent;
        if (hadIndent) {
            unIndentTo(indentLevel);
        }

        if (hadIndent || hadPreIndent || withLine) line();

        return this;
    }

    public HtmlWriter line() {
        if (lastChar != 0 && lastChar != '\n' && !delayedEOL) {
            append("\n");
        }
        return this;
    }

    public HtmlWriter lineIf(boolean predicate) {
        if (predicate) return line();
        return this;
    }

    public HtmlWriter indent() {
        line();

        indent++;
        indentPrefix += indentSizePrefix;
        return this;
    }

    public HtmlWriter unIndent() {
        if (indent > 0) {
            line();
            indent--;
            if (indent * indentSize > 0) indentPrefix = indentPrefix.substring(0, indent * indentSize);
            else indentPrefix = "";
        }
        return this;
    }

    public HtmlWriter withCondIndent() {
        indentIndentingChildren = true;
        return this;
    }

    public HtmlWriter withCondLine() {
        lineOnChildText = true;
        return this;
    }

    public HtmlWriter unIndentTo(int indentSize) {
        delayedIndent = false;
        while (indentSize < indent) unIndent();
        return this;
    }

    public HtmlWriter openPre() {
        preNesting++;
        return this;
    }

    public HtmlWriter closePre() {
        if (preNesting <= 0) {
            throw new IllegalStateException("Close <pre> context with none open");
        }
        preNesting--;
        return this;
    }

    protected void append(String s) {
        if (s.length() == 0) return;
        //appendCount++;
        appendCount++;

        if (delayedEOL) {
            delayedEOL = false;
            if (s.charAt(0) != '\n') append("\n" + prefix);
        }

        if ((!indentPrefix.isEmpty() || !prefix.isEmpty()) && preNesting <= 0) {
            // convert \n to \n + indent except for the last one
            // also if the last is \n then prefix indent size

            try {
                int lastPos = 0;
                boolean lastWasEOL = lastChar == '\n';

                while (lastPos < s.length()) {
                    int pos = s.indexOf('\n', lastPos);
                    if (pos < 0 || pos == s.length() - 1) {
                        if (lastWasEOL) buffer.append(prefix).append(indentPrefix);
                        buffer.append(s.substring(lastPos));
                        break;
                    }

                    if (pos > lastPos) {
                        if (lastWasEOL) buffer.append(prefix).append(indentPrefix);
                        buffer.append(s.substring(lastPos, pos));
                    }

                    buffer.append("\n");
                    lastWasEOL = true;
                    lastPos = pos + 1;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            int length = s.length();
            if (length != 0) {
                lastChar = s.charAt(length - 1);
            }
        } else {
            try {
                if (lastChar == '\n' && !prefix.isEmpty()) buffer.append(prefix);
                buffer.append(s);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            int length = s.length();
            if (length != 0) {
                lastChar = s.charAt(length - 1);
            }
        }
    }
}
