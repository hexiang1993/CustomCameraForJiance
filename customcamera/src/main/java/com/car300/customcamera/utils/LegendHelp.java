package com.car300.customcamera.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.car300.customcamera.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xhe on 2016/10/27.
 * 图例
 */

public class LegendHelp {

    public static PopupWindow getPop(Context context, int type) {
        View view = LayoutInflater.from(context).inflate(R.layout.pop_legend_help, null);
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        TextView title_ = (TextView) view.findViewById(R.id.title);
        TextView text = (TextView) view.findViewById(R.id.text);
        ImageView img = (ImageView) view.findViewById(R.id.img);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        String data = "";
        try {
            JSONObject object = new JSONObject(legendJson);
            JSONObject obj = object.getJSONObject(type+"");
            title_.setText(obj.getString("title"));
            text.setText(Html.fromHtml(obj.getString("text")));
            img.setBackgroundResource(getDrawableId(context, obj.getString("img")));
        } catch (JSONException e) {
            return null;//没有图例的时候则返回null的popwindow
        }
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        return popupWindow;
    }

    public static int getDrawableId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString,
                "drawable", paramContext.getPackageName());
    }

    public static String legendJson = "{\n" +
            "  \"1\": {\n" +
            "    \"title\": \"左前45度\",\n" +
            "    \"text\": \"①请站在车辆左前方45度角进行拍摄。<br>②拍摄的照片，需看见车顶（是否有天窗），车头（左右两侧大灯），车身、左侧前后轮胎。\",\n" +
            "    \"img\": \"legend_left_front_45\"\n" +
            "  },\n" +
            "  \"2\": {\n" +
            "    \"title\": \"正侧面\",\n" +
            "    \"text\": \"①在车辆侧面正前方拍摄。<br>②需看见车辆一侧大灯、前后轮、一侧车窗等。\",\n" +
            "    \"img\": \"legend_front_side\"\n" +
            "  },\n" +
            "  \"3\": {\n" +
            "    \"title\": \"头部正面\",\n" +
            "    \"text\": \"①在车辆头部正前方拍摄。<br>②需看见左右大灯、车头主要部位清楚。\",\n" +
            "    \"img\": \"legend_head_front2\"\n" +
            "  },\n" +
            "  \"4\": {\n" +
            "    \"title\": \"尾部正面\",\n" +
            "    \"text\": \"①在车辆尾部正后方拍摄。<br>②需看见尾部大灯、车尾主要部位清楚。\",\n" +
            "    \"img\": \"legend_tail_front2\"\n" +
            "  },\n" +
            "  \"5\": {\n" +
            "    \"title\": \"车辆大灯\",\n" +
            "    \"text\": \"①拍摄车辆前大灯。要求大灯清晰、居画面中央位置。\",\n" +
            "    \"img\": \"legend_headlight2\"\n" +
            "  },\n" +
            "  \"6\": {\n" +
            "    \"title\": \"侧面前轮\",\n" +
            "    \"text\": \"①拍摄车辆侧面前轮，轮胎轮毂清晰、居画面中央位置。\",\n" +
            "    \"img\": \"legend_front_wheel\"\n" +
            "  },\n" +
            "  \"7\": {\n" +
            "    \"title\": \"前排车门正面\",\n" +
            "    \"text\": \"①完全打开车辆前排车门并拍摄，完全展示车窗下至车门底部区域。\",\n" +
            "    \"img\": \"legend_front_door\"\n" +
            "  },\n" +
            "  \"8\": {\n" +
            "    \"title\": \"前车门密封条\",\n" +
            "    \"text\": \"①轻微拉起前车门密封条并拍摄。\",\n" +
            "    \"img\": \"legend_weatherstrip\"\n" +
            "  },\n" +
            "  \"9\": {\n" +
            "    \"title\": \"安全带根部\",\n" +
            "    \"text\": \"①把安全带拉伸至最长。<br>②拍摄安全带拉出的部分。<br>③安全带主驾侧和副驾侧任意一侧即可。\",\n" +
            "    \"img\": \"legend_seat_belts\"\n" +
            "  },\n" +
            "  \"10\": {\n" +
            "    \"title\": \"车内前排\",\n" +
            "    \"text\": \"①在车辆前车门位置，正对车内前排拍摄。<br>②需看见方向盘、油门踏板、前排车座、后视镜等。\",\n" +
            "    \"img\": \"legend_inner_front\"\n" +
            "  },\n" +
            "  \"11\": {\n" +
            "    \"title\": \"车内后排\",\n" +
            "    \"text\": \"①在车辆后车门位置，正对车内后排拍摄。<br>②需完整展示后排座椅。\",\n" +
            "    \"img\": \"legend_inner_back\"\n" +
            "  },\n" +
            "  \"12\": {\n" +
            "    \"title\": \"仪表台\",\n" +
            "    \"text\": \"①拍摄的照片，需含有方向盘、仪表盘、显示屏、收音机、空调控制器、换挡杆等。<br>②建议坐后排中间位置进行拍摄。\",\n" +
            "    \"img\": \"legend_dashboard\"\n" +
            "  },\n" +
            "  \"13\": {\n" +
            "    \"title\": \"车内顶棚\",\n" +
            "    \"text\": \"①打开后车门，在后车门底部仰拍车内顶棚。<br>②需看见车内顶棚的主要区域，有天窗的需要拍摄到天窗。\",\n" +
            "    \"img\": \"legend_platfond\"\n" +
            "  },\n" +
            "  \"14\": {\n" +
            "    \"title\": \"变速箱杆\",\n" +
            "    \"text\": \"①在前排车门一侧，拍摄变速箱杆。<br>②要求变速箱杆清晰，居画面中央位置。\",\n" +
            "    \"img\": \"legend_gear_lever\"\n" +
            "  },\n" +
            "  \"15\": {\n" +
            "    \"title\": \"油门踏板\",\n" +
            "    \"text\": \"①在左侧前排车门一侧，拍摄油门踏板。<br>②油门踏板部分光线较暗，需要注意提高拍摄曝光度。\",\n" +
            "    \"img\": \"legend_accelerator_pedal\"\n" +
            "  },\n" +
            "  \"16\": {\n" +
            "    \"title\": \"发动机舱\",\n" +
            "    \"text\": \"①打开前发动机舱盖，站在车头正前方。<br>②斜下45度角对发动机舱全景拍摄，需包含左右两侧翼子板，下至保险杠，上至机舱盖铰链连接处。\",\n" +
            "    \"img\": \"legend_engine_bay2\"\n" +
            "  },\n" +
            "  \"17\": {\n" +
            "    \"title\": \"后备箱\",\n" +
            "    \"text\": \"①打开后备箱盖至最大角。<br>②拍摄的照片，应看到后备箱盖左上右三侧边缘及下侧铰链连接处。\",\n" +
            "    \"img\": \"legend_trunk\"\n" +
            "  }\n" +
            "}";
}
