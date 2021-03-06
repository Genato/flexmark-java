package com.vladsch.flexmark.ext.tables;

import com.vladsch.flexmark.ast.*;
import com.vladsch.flexmark.ast.util.TextNodeConverter;
import com.vladsch.flexmark.util.sequence.BasedSequence;

/**
 * Table cell of a {@link TableRow} containing inline nodes.
 */
public class TableCell extends CustomNode implements DelimitedNode {
    protected BasedSequence openingMarker = BasedSequence.NULL;
    protected BasedSequence text = BasedSequence.NULL;
    protected BasedSequence closingMarker = BasedSequence.NULL;

    private boolean header;
    private Alignment alignment;
    private int span = 1;

    public void trimWhiteSpace() {
        Node child = getFirstChild();
        while (child != null && child instanceof WhiteSpace) {
            Node next = child.getNext();
            child.unlink();
            child = next;
        }

        child = getLastChild();
        while (child != null && child instanceof WhiteSpace) {
            Node next = child.getPrevious();
            child.unlink();
            child = next;
        }
    }

    public void mergeWhiteSpace() {
        boolean hadWhitespace = false;
        Node child = getFirstChild();

        while (child != null && child instanceof WhiteSpace) {
            Node next = child.getNext();
            Text text = new Text(child.getChars());
            child.insertBefore(text);
            child.unlink();
            child = next;
            hadWhitespace = true;
        }

        child = getLastChild();
        while (child != null && child instanceof WhiteSpace) {
            Node previous = child.getPrevious();
            Text text = new Text(child.getChars());
            child.insertBefore(text);
            child.unlink();
            child = previous;
            hadWhitespace = true;
        }

        if (hadWhitespace) {
            TextNodeConverter.mergeTextNodes(this);
        }
    }

    @Override
    public BasedSequence getOpeningMarker() {
        return openingMarker;
    }

    @Override
    public void setOpeningMarker(BasedSequence openingMarker) {
        this.openingMarker = openingMarker;
    }

    @Override
    public BasedSequence getText() {
        return text;
    }

    @Override
    public void setText(BasedSequence text) {
        this.text = text;
    }

    @Override
    public BasedSequence getClosingMarker() {
        return closingMarker;
    }

    @Override
    public void setClosingMarker(BasedSequence closingMarker) {
        this.closingMarker = closingMarker;
    }

    public int getSpan() {
        return span;
    }

    public void setSpan(int span) {
        this.span = span;
    }

    @Override
    public BasedSequence[] getSegments() {
        return new BasedSequence[] { openingMarker, text, closingMarker };
    }

    @Override
    public void getAstExtra(StringBuilder out) {
        if (alignment != null) out.append(" ").append(alignment);
        if (header) out.append(" header");
        if (span > 1) out.append(" span");
        delimitedSegmentSpanChars(out, openingMarker, text, closingMarker, "text");
    }

    public TableCell() {
    }

    public TableCell(BasedSequence chars) {
        super(chars);
    }

    /**
     * @return whether the cell is a header or not
     */
    public boolean isHeader() {
        return header;
    }

    public void setHeader(boolean header) {
        this.header = header;
    }

    /**
     * @return the cell alignment
     */
    public Alignment getAlignment() {
        return alignment;
    }

    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    /**
     * How the cell is aligned horizontally.
     */
    public enum Alignment {
        LEFT, CENTER, RIGHT
    }
}
