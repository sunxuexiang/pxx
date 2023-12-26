package com.wanmi.sbc.util;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

public class CustomPDFTextStripper extends PDFTextStripper {
    private List<String> targetContents;
    private List<Coordinate> coordinates;
    private StringBuilder currentBlock;
    private int pageNum;

    public CustomPDFTextStripper(List<String> targetContents,int pageNum) throws IOException {
        super();
        this.pageNum =pageNum;
        this.targetContents = targetContents;
        this.coordinates = new ArrayList<>();
        this.currentBlock = new StringBuilder();
    }

    @Override
    protected void processTextPosition(TextPosition text) {
        String content = text.getUnicode();
        currentBlock.append(content);

        for (String targetContent : targetContents) {
            if (currentBlock.toString().contains(targetContent)) {
                float x = text.getX() ; // 根据缩放比例调整X坐标
                float y = text.getY() ; // 根据缩放比例调整Y坐标

                pageNum++;

                // 保存坐标信息
                coordinates.add(new Coordinate(pageNum, x, y));
                // 清空当前文本块缓存，以便匹配下一个文本块
                currentBlock.setLength(0);
            }
        }

        super.processTextPosition(text);
    }


    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

}
