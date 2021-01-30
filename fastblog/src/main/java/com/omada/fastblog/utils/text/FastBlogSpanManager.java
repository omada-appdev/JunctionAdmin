package com.omada.fastblog.utils.text;

import android.text.Spannable;
import android.text.style.CharacterStyle;
import android.text.style.MetricAffectingSpan;
import android.text.style.StyleSpan;

import java.util.ArrayList;

public class FastBlogSpanManager {

    private FastBlogSpanManager(){}

    public synchronized static void removeOne(Spannable spannable,
                          int startSelection, int endSelection, Class<?> style){

        ArrayList<SpanParts> spansParts = getSpanParts(spannable, startSelection, endSelection);
        removeOneSpan(spannable, startSelection, endSelection, style);
        restoreSpans(spannable, spansParts);
    }

    public synchronized static void removeStyle(Spannable spannable,
                            int startSelection, int endSelection, int styleToRemove){

        ArrayList<SpanParts> spansParts = getSpanParts(spannable, startSelection, endSelection);
        removeStyleSpan(spannable, startSelection, endSelection, styleToRemove);
        restoreSpans(spannable, spansParts);
    }

    public synchronized static void removeAll(Spannable spannable, int startSelection, int endSelection){

        ArrayList<SpanParts> spansParts = getSpanParts(spannable, startSelection, endSelection);
        removeAllSpans(spannable, startSelection, endSelection);
        restoreSpans(spannable, spansParts);
    }

    protected synchronized static void restoreSpans(Spannable spannable, ArrayList<SpanParts> spansParts){

        for (SpanParts spanParts : spansParts) {
            if(spanParts.part1.canApply())
                spannable.setSpan(spanParts.part1.span, spanParts.part1.start,
                        spanParts.part1.end,spanParts.spanFlag);
            if(spanParts.part2.canApply())
                spannable.setSpan(spanParts.part2.span, spanParts.part2.start,
                        spanParts.part2.end, spanParts.spanFlag);
        }
    }

    protected synchronized static void removeAllSpans(Spannable spannable,int startSelection, int endSelection) {

        Object[] spansToRemove = spannable.getSpans(startSelection, endSelection, Object.class);
        for(Object span: spansToRemove){
            if(span instanceof CharacterStyle)
                spannable.removeSpan(span);
        }
    }

    protected synchronized static void removeOneSpan(Spannable spannable,int startSelection, int endSelection,
                                 Class<?> style) {

        CharacterStyle[] spansToRemove = spannable.getSpans(startSelection, endSelection, CharacterStyle.class);
        for(CharacterStyle span: spansToRemove){
            if(span.getUnderlying().getClass() == style )
                spannable.removeSpan(span);
        }
    }

    protected synchronized static void removeStyleSpan(Spannable spannable, int startSelection,
                                   int endSelection, int styleToRemove) {

        MetricAffectingSpan[] spans = spannable.getSpans(startSelection, endSelection, MetricAffectingSpan.class);
        for(MetricAffectingSpan span: spans){
            int stylesApplied;
            int stylesToApply;
            int spanStart;
            int spanEnd;
            int spanFlag;
            Object spanUnd = span.getUnderlying();
            if(spanUnd instanceof StyleSpan){
                spanFlag = spannable.getSpanFlags(spanUnd);
                stylesApplied = ((StyleSpan) spanUnd).getStyle();
                stylesToApply = stylesApplied & ~styleToRemove;

                spanStart = spannable.getSpanStart(span);
                spanEnd = spannable.getSpanEnd(span);
                if(spanEnd >= 0 && spanStart >= 0){
                    spannable.removeSpan(span);
                    spannable.setSpan(new StyleSpan(stylesToApply), spanStart, spanEnd,spanFlag);
                }
            }
        }
    }

    protected synchronized static ArrayList<SpanParts> getSpanParts(Spannable spannable,
                                                int startSelection,int endSelection){

        ArrayList<SpanParts> spansParts = new ArrayList<>();
        Object[] spans = spannable.getSpans(startSelection, endSelection, Object.class);
        for(Object span: spans){
            if(span instanceof CharacterStyle){
                SpanParts spanParts = new SpanParts();
                int spanStart = spannable.getSpanStart(span);
                int spanEnd = spannable.getSpanEnd(span);
                if(spanStart == startSelection && spanEnd == endSelection) continue;
                spanParts.spanFlag = spannable.getSpanFlags(span);
                spanParts.part1.span = CharacterStyle.wrap((CharacterStyle) span);
                spanParts.part1.start = spanStart;
                spanParts.part1.end = startSelection;
                spanParts.part2.span = CharacterStyle.wrap((CharacterStyle) span);
                spanParts.part2.start = endSelection;
                spanParts.part2.end = spanEnd;
                spansParts.add(spanParts);
            }
        }
        return spansParts;
    }

    private static class SpanParts{
        int spanFlag;
        Part part1;
        Part part2;
        SpanParts() {
            part1 = new Part();
            part2 = new Part();
        }
    }
    private static class Part{
        CharacterStyle span;
        int start;
        int end;
        boolean canApply() {
            return start < end;
        }
    }

}
