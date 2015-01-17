package com.hustascii.ydfm.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.hustascii.ydfm.R;
import com.hustascii.ydfm.util.Crawls;
import com.hustascii.ydfm.util.Globles;
import com.hustascii.ydfm.view.RefreshLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.apache.http.Header;

import me.biubiubiu.justifytext.library.JustifyTextView;

public class ArticleActivity extends ActionBarActivity {

    private JustifyTextView articleText;
    private String article;
    private String contentUrl;
    private ProgressWheel wheel;
    private SwipeRefreshLayout refreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);


        Intent intent = getIntent();
        contentUrl = intent.getStringExtra("url");
        articleText = (JustifyTextView)findViewById(R.id.article);
//        wheel = new ProgressWheel(this);
//        wheel.setBarColor(Color.RED);
//        wheel.spin();

        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);

        refreshLayout.setColorScheme(android.R.color.holo_red_light, android.R.color.holo_green_light,
                android.R.color.holo_blue_bright, android.R.color.holo_orange_light);
        refreshLayout.setRefreshing(true);
        if(article==null||article.equals("")) {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(Globles.BASE_URL+contentUrl.substring(1), new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    super.onSuccess(statusCode, headers, responseBody);
                    article = Crawls.getArticleUrl(new String(responseBody));
                    article = article.replace("<br />","\n").replace("&nbsp;", " ").replace("\n\n","\n");
                    /*article = "你不能因为一个人常去夜店说他不好；你也不能看到一个人经常吊儿郎当，就说他没前途；你更不能看到一个人总是笑嘻嘻的，就觉得怎么伤害他，他都不会往心里去。每个人都有每个人的价值观，这个世界本就光怪陆离，你只需要向着自己坚持的正确的价值观走到最后就好了，自有人在终点等你。\n" +
                            " \n" +
                            "曾经看到“你看别人不顺眼，是因为你修养不够”时，我认为这是一句很扯淡的话——别人的错误与我何干？\n" +
                            " \n" +
                            "以前为了点小事都能和别人吵起来，吵到后来就忘了争吵的原因是什么，只拼命想要证明自己，到最后伤人伤己。后来发觉少说一句话又不会死，一个人越恶毒，遭受的回馈也就越恶毒，给别人台阶下，就是给自己台阶下。与其嘴上争论别人是错误的，不如行动证明自己是正确的，争吵是这世界上最浪费时间的事。\n" +
                            " \n" +
                            "每个人自有每个人的价值观，尝试理解，如果实在理解不了那就转头离去。你永远无法说服别人，就像别人无法说服你一样，每个人总有每个人的坚持。\n" +
                            " \n" +
                            "我想正因为如此，能遇到和你观念一致、愿意停下脚步陪你的同类，是这世上最幸运的事。\n" +
                            " \n" +
                            "始终记得《玛丽与马克思》中最让我感动的那段对白：\n" +
                            " \n" +
                            "“我原谅你是因为你不是完人，你并不是完美无瑕，而我也是，人无完人，即便是那些在门外乱扔杂物的人。我年轻时想变成任何一个人，除了自己，伯纳德哈斯豪夫医生说，如果我在一个孤岛上，那么我就要适应一个人生活，只有椰子和我。他说我必须要接受我自己，我的缺点和我的全部，我们无法选择自己的缺点，它们也是我们的一部分然，而我们必须适应它们。然而我们能选择我们的朋友，我很高兴选择了你。每个人的人生就是一条很长的人行道，有的很整洁；而有的像我一样，有裂缝、香蕉皮和烟头。你的人行道像我一样，但是没有我的有这么多裂缝。有朝一日，希望你我的人行道会相交在一起，到时候我们可以分享一罐炼乳。你是我最好的朋友。你是我唯一的朋友。”\n" +
                            " \n" +
                            "每个人产生价值观的过程都不尽相同，唯一的共同点就是它远比你想的更漫长并且根深蒂固。我以前无法认同一个人的价值观时，就想以否定他的形式来证明自己是正确的。\n" +
                            " \n" +
                            "现在我开始觉得：每个人的价值观都是他自身成长的结果，你无法通过三言两语把它否定。好比说你不能因为看一个人常去夜店就说他不好；你不能因为一个人不走所谓的正途就认定他没前途；你更不能看到一个人总是笑嘻嘻的，就认为他是铜墙铁壁、百毒不侵。\n" +
                            " \n" +
                            "正如《玛丽与马克思》中所说，人无完人，而每个人的人生路都是一条很长的人行道，有的很整洁，有的则充满裂缝。但总有一天，我们的人行道会相遇。等到那时，你的人行道是怎样的，你的过去经历了多少苦，都无所谓了。\n" +
                            " \n" +
                            "爱情也是如此，真正的爱情是两个人在尽可能能够做自己的情况下，在两人共同成长的基础上的情感共振。\n" +
                            " \n" +
                            "在刚出发的时候，我们都处于道路的起点，这个起点有很多人，我们共同生活在这样一个村落里。然后你开始选择你的路，慢慢地你变得孤身一人，你开始觉得人生无比漫长。\n" +
                            " \n" +
                            "然而就像所有河流终将汇入大海，当你走得足够远的时候，你总会遇到和你或同行或交叉的人行道。所谓的共鸣是建立在你们的某些观念在某种程度上是相似的基础上的，你是什么样的人，就会喜欢什么样的东西、遇到什么样的人、被什么样的东西感动。\n" +
                            " \n" +
                            "那些交会的人行道，只有你走到足够远的时候，你才会看到；那些你寻求的共鸣，只有你本身具备了很多东西，才能够遇到。\n" +
                            " \n" +
                            "不管这个共振有效期有多长，不管这个人是否来了又走，始终对有这样的人出现这件事，心存感激。因为是这些人，才让你免于在孤独中徘徊。人生自是一场看不到头的颠沛流离，但一旦找到，即使岁月反复，也不觉得漫长了。\n" +
                            " \n" +
                            "愿你可以找到那个与你长期共振，可以相互诉说废话的人。";*/
                    Log.v("article", article);

                    if(article==null||article.equals("")) {
                        Toast.makeText(getApplicationContext(), "网页解析错误", Toast.LENGTH_SHORT).show();
                    }else{
                        articleText.setText(article);
                    }
//                    refreshLayout.setRefreshing(false);

//                    wheel.stopSpinning();
                }
            });
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_article, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
