package com.sf.DummyTests;

import com.sf.BaseClass.BaseClass;
import org.testng.annotations.Test;

public class DummyTest2 extends BaseClass {

    @Test
    public void dummyTest() {
        String title=getDriver().getTitle();
        assert title.equals("OrangeHRM"):"Test failed Title Not Matching";

        System.out.println("Test Passed");

    }
}
