/*
 * Copyright 2017 Babak Farhang
 */
package com.gnahraf.print;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.Arrays;

import com.gnahraf.util.list.IntArrayList;

/**
 *
 */
public class TablePrint extends PrintSupport {
  
  
  private final int[] columnWidths;
  
  @SuppressWarnings("serial")
  private NumberFormat numberFormat =
      new DecimalFormat("#,###.##") {

        @Override
        public StringBuffer format(double number, StringBuffer result, FieldPosition fieldPosition) {
          if (number >= 0)
            result.append(' ');
          return super.format(number, result, fieldPosition);
        }

        @Override
        public StringBuffer format(long number, StringBuffer result, FieldPosition fieldPosition) {
          if (number >= 0)
            result.append(' ');
          return super.format(number, result, fieldPosition);
        }
      };

  /**
   * 
   */
  public TablePrint(int... columnWidths) {
    this(System.out, columnWidths);
  }

  /**
   * @param out
   */
  public TablePrint(PrintStream out, int... columnWidths) {
    super(out);
    this.columnWidths = columnWidths.clone();
    
    for (int index = columnWidths.length; index-- > 0; )
      if (this.columnWidths[index] < 0)
        throw new IllegalArgumentException(new IntArrayList(columnWidths).toString());
  }
  
  
  public void setNumberFormat(NumberFormat numberFormat) {
    if (numberFormat == null)
      throw new IllegalArgumentException("null");
    this.numberFormat = numberFormat;
  }
  
  
  /**
   * Prints a table row. To be invoked at beginning of new line.
   */
  public void printRow(Object... cells) {
    if (cells.length > columnWidths.length)
      throw new IllegalArgumentException("too many args: " + Arrays.asList(cells));
    
    for (int index = 0; index < cells.length; ++index) {
      Object cell = cells[index];
      String string;
      if (cell instanceof Number)
        string = numberFormat.format(cell);
      else if (cell == null)
        string = "";
      else
        string = cell.toString();
      print(string);
      printSpaces(columnWidths[index] - string.length());
    }
    println();
  }
  
  
  /**
   * Prints a table edge. To be invoked at beginning of new line.
   */
  public void printHorizontalTableEdge(char c) {
    printChar(c, getRowWidth());
    println();
  }
  
  
  
  public int getRowWidth() {
    int width = 0;
    for (int index = columnWidths.length; index-- > 0; )
      width += columnWidths[index];
    return width;
  }

}
