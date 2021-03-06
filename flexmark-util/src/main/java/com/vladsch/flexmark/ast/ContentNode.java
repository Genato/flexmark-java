package com.vladsch.flexmark.ast;

import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.flexmark.util.sequence.SegmentedSequence;
import com.vladsch.flexmark.util.sequence.SubSequence;

import java.util.List;

public abstract class ContentNode extends Node implements Content {
    protected List<BasedSequence> lineSegments = BasedSequence.EMPTY_LIST;

    public ContentNode() {

    }

    public ContentNode(BasedSequence chars) {
        super(chars);
    }

    public ContentNode(BasedSequence chars, List<BasedSequence> lineSegments) {
        super(chars);
        this.lineSegments = lineSegments;
    }

    public ContentNode(List<BasedSequence> lineSegments) {
        this(getSpanningChars(lineSegments), lineSegments);
    }

    public ContentNode(BlockContent blockContent) {
        this(blockContent.getSpanningChars(), blockContent.getLines());
    }

    public void setContent(BasedSequence chars, List<BasedSequence> lineSegments) {
        setChars(chars);
        this.lineSegments = lineSegments;
    }

    public void setContent(List<BasedSequence> lineSegments) {
        this.lineSegments = lineSegments;
        setChars(getSpanningChars());
    }

    public void setContent(BlockContent blockContent) {
        setChars(blockContent.getSpanningChars());
        this.lineSegments = blockContent.getLines();
    }

    @Override
    public BasedSequence getSpanningChars() {
        return getSpanningChars(lineSegments);
    }

    private static BasedSequence getSpanningChars(List<BasedSequence> lineSegments) {
        return lineSegments.size() > 0 ? new SubSequence(lineSegments.get(0).getBase(), lineSegments.get(0).getStartOffset(), lineSegments.get(lineSegments.size() - 1).getEndOffset()) : BasedSequence.NULL;
    }

    @Override
    public int getLineCount() {
        return lineSegments.size();
    }

    @Override
    public BasedSequence getLineChars(int index) {
        return lineSegments.get(index);
    }

    @Override
    public List<BasedSequence> getContentLines() {
        return getContentLines(0, lineSegments.size());
    }

    @Override
    public List<BasedSequence> getContentLines(int startLine, int endLine) {
        return lineSegments.subList(startLine, endLine);
    }

    @Override
    public BasedSequence getContentChars() {
        return SegmentedSequence.of(lineSegments, getChars().subSequence(getChars().length()));
    }

    @Override
    public BasedSequence getContentChars(int startLine, int endLine) {
        return SegmentedSequence.of(getContentLines(startLine, endLine), getChars());
    }

    public void setContentLines(List<BasedSequence> contentLines) {
        this.lineSegments = contentLines;
    }
}
