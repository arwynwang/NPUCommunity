package com.example.npucommunity;

import com.stephentuso.welcome.*;

public class MyWelcomeActivity extends WelcomeActivity{

    @Override
    protected WelcomeConfiguration configuration() {
        return new WelcomeConfiguration.Builder(this)
                .defaultTitleTypefacePath("Montserrat-Bold.ttf")
                .defaultHeaderTypefacePath("Montserrat-Bold.ttf")
                .page(new BasicPage(R.drawable.ic_front_desk_white,
                        "欢迎",
                        "NPUCommunity 社交平台")
                        .background(R.color.orange_background)
                )

                .page(new BasicPage(R.drawable.ic_thumb_up_white,
                        "简单易用",
                        "实现区域式社交，快速了解区域信息")
                        .background(R.color.red_background)
                )
                .page(new BasicPage(R.drawable.ic_edit_white,
                        "版本号1.0",
                        "免流的局域网通讯、文件传输和“朋友圈”")
                        .background(R.color.blue_background)
                )
                .swipeToDismiss(true)
                .build();
    }
}
