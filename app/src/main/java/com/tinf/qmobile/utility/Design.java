package com.tinf.qmobile.utility;

import com.tinf.qmobile.R;
import java.util.HashMap;
import java.util.Map;

public class Design {

    public static Map<String, Integer> icons = new HashMap<>();

    public static int parseIcon(String ext) {
        if (icons.isEmpty()) {
            icons.put(".pdf", R.drawable.ic_pdf);
            icons.put(".doc", R.drawable.ic_doc);
            icons.put(".docx", R.drawable.ic_doc);
            icons.put(".ppt", R.drawable.ic_ppt);
            icons.put(".pptx", R.drawable.ic_ppt);
            icons.put(".xls", R.drawable.ic_xls);
            icons.put(".xlsx", R.drawable.ic_xls);
            icons.put(".zip", R.drawable.ic_zip);
            icons.put(".rtf", R.drawable.ic_rtf);
            icons.put(".txt", R.drawable.ic_txt);
            icons.put(".csv", R.drawable.ic_csv);
            icons.put(".svg", R.drawable.ic_svg);
            icons.put(".rar", R.drawable.ic_comp);
            icons.put(".7z", R.drawable.ic_comp);
            icons.put(".css", R.drawable.ic_css);
            icons.put(".dbf", R.drawable.ic_dbf);
            icons.put(".dwg", R.drawable.ic_dwg);
            icons.put(".exe", R.drawable.ic_exe);
            icons.put(".fla", R.drawable.ic_fla);
            icons.put(".html", R.drawable.ic_html);
            icons.put(".xml", R.drawable.ic_xml);
            icons.put(".iso", R.drawable.ic_iso);
            icons.put(".js", R.drawable.ic_js);
            icons.put(".jpg", R.drawable.ic_jpg);
            icons.put(".jpeg", R.drawable.ic_jpg);
            icons.put(".json", R.drawable.ic_json);
            icons.put(".mp3", R.drawable.ic_mp3);
            icons.put(".mp4", R.drawable.ic_mp4);
            icons.put(".ai", R.drawable.ic_ai);
            icons.put(".avi", R.drawable.ic_avi);
            icons.put(".png", R.drawable.ic_png);
            icons.put(".psd", R.drawable.ic_psd);
        }

        Integer icon = icons.get(ext);

        if (icon == null)
            icon = R.drawable.ic_file;

        return icon;
    }

}
