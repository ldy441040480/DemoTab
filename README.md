# DemoTab
字体颜色大小改变的 indicator

![image](https://github.com/ldy441040480/DemoTab/blob/master/gif/a.gif)   ![image](https://github.com/ldy441040480/DemoTab/blob/master/gif/b.gif)   
![image](https://github.com/ldy441040480/DemoTab/blob/master/gif/c.gif)   

   <declare-styleable name="ScaleLayout">
        <attr name="bottom_color" format="color|reference" />
        <attr name="bottom_height" format="dimension" />
        <attr name="line_right" format="dimension" />
        <attr name="line_left" format="dimension" />
        <attr name="line_bottom" format="dimension" />
        <attr name="fit_text" format="boolean" />
    </declare-styleable>
    
    
    mTabLayout = (ScaleTabLayout) findViewById(R.id.layout_tab);
    mTabLayout.setViewPager(mViewPager);
