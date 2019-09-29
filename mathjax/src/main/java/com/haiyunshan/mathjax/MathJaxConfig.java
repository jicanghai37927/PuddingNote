package com.haiyunshan.mathjax;

import android.webkit.JavascriptInterface;

/**
 * http://docs.mathjax.org/en/latest/options/
 * for more information
 *
 * Created by timfreiheit on 06.06.15.
 */
public class MathJaxConfig {

    public enum Output{
        SVG("output/SVG"),
        HTML_CSS("output/HTML-CSS"),
        CommonHTML("output/CommonHTML"),
        NativeMML("output/NativeMML")
        ;

        String value;

        Output(String s) {
            value = s;
        }
    }

    public enum Input{
        TeX("input/TeX"),
        MathML("input/MathML"),
        AsciiMath("input/AsciiMath")
        ;

        String value;

        Input(String s) {
            value = s;
        }
    }

    private String input = Input.TeX.value;
    private String output = Output.HTML_CSS.value;
    private int outputScale = 100;
    private int minScaleAdjust = 100;
    private boolean automaticLinebreaks = false;
    private int blacker = 1;

    public MathJaxConfig() {

    }

    @JavascriptInterface
    public String getInput(){
        return input;
    }

    public void setInput(Input input){
        this.input = input.value;
    }

    @JavascriptInterface
    public String getOutput(){
        return output;
    }

    public void setOutput(Output output){
        this.output = output.value;
    }

    @JavascriptInterface
    public int getOutputScale() {
        return outputScale;
    }

    public void setOutputScale(int outputScale) {
        this.outputScale = outputScale;
    }

    @JavascriptInterface
    public int getMinScaleAdjust(){
        return minScaleAdjust;
    }

    public void setMinScaleAdjust(int scale){
        this.minScaleAdjust = scale;
    }

    @JavascriptInterface
    public boolean getAutomaticLinebreaks(){
        return automaticLinebreaks;
    }

    public void setAutomaticLinebreaks(boolean b){
        this.automaticLinebreaks = b;
    }

    @JavascriptInterface
    public int getBlacker(){
        return blacker;
    }

    public void setBlacker(int blacker){
        this.blacker = blacker;
    }

}
