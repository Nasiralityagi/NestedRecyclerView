package com.nesstech.metube.Utility;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nesstech.metube.R;
import com.nesstech.metube.youmodel.Item;

public class Utills {
    public static String parseDuration(String duration) {
        String time = "";
        boolean hourexists = false, minutesexists = false, secondsexists = false;
        if (duration.contains("H"))
            hourexists = true;
        if (duration.contains("M"))
            minutesexists = true;
        if (duration.contains("S"))
            secondsexists = true;
        if (hourexists) {
            String hour;
            hour = duration.substring(duration.indexOf("T") + 1,
                    duration.indexOf("H"));
            if (hour.length() == 1)
                hour = "0" + hour;
            time += hour + ":";
        }
        if (minutesexists) {
            String minutes;
            if (hourexists)
                minutes = duration.substring(duration.indexOf("H") + 1,
                        duration.indexOf("M"));
            else
                minutes = duration.substring(duration.indexOf("T") + 1,
                        duration.indexOf("M"));
            if (minutes.length() == 1)
                minutes = "0" + minutes;
            time += minutes + ":";
        } else {
            time += "00:";
        }
        if (secondsexists) {
            String seconds;
            if (hourexists) {
                if (minutesexists)
                    seconds = duration.substring(duration.indexOf("M") + 1,
                            duration.indexOf("S"));
                else
                    seconds = duration.substring(duration.indexOf("H") + 1,
                            duration.indexOf("S"));
            } else if (minutesexists)
                seconds = duration.substring(duration.indexOf("M") + 1,
                        duration.indexOf("S"));
            else
                seconds = duration.substring(duration.indexOf("T") + 1,
                        duration.indexOf("S"));
            if (seconds.length() == 1)
                seconds = "0" + seconds;
            time += seconds;
        }
        return time;
    }


    public static String parseDate(String id) {
        if (null == id)
            return "14 Feb 17";

        String month = id.substring(5, 7);
        String date = id.substring(8, 10);
        String year = id.substring(0, 4);
        String monthname = "";
        String properDate;

        switch (month) {
            case "01":
                monthname = "Jan";
                break;
            case "02":
                monthname = "Feb";
                break;
            case "03":
                monthname = "Mar";
                break;
            case "04":
                monthname = "Apr";
                break;
            case "05":
                monthname = "May";
                break;
            case "06":
                monthname = "Jun";
                break;
            case "07":
                monthname = "Jul";
                break;
            case "08":
                monthname = "Aug";
                break;
            case "09":
                monthname = "Sep";
                break;
            case "10":
                monthname = "Oct";
                break;
            case "11":
                monthname = "Nov";
                break;
            case "12":
                monthname = "Dec";
                break;
        }
        properDate = date + " " + monthname + " " + year;
        return properDate;
    }


    public static void setThumbVideo(Context context, String videoImage, ProgressBar progress, ImageView itemImage) {
        final ProgressBar mProgress = progress;
        Glide.with(context)
                .load(videoImage)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        // TODO: 08/11/16 handle failure
                        mProgress.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        // image ready, hide progress now
                        mProgress.setVisibility(View.GONE);
                        return false;   // return false if you want Glide to handle everything else.
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image
                .centerCrop()
                .crossFade()
                .into(itemImage);

    }

    public static String getVideoImage(Item result) {
        return result.getSnippet().getThumbnails().getHigh().getUrl() == null ?
                (result.getSnippet().getThumbnails().getMedium().getUrl() == null ?
                        (result.getSnippet().getThumbnails().getDefault().getUrl() == null ?
                                result.getSnippet().getThumbnails().getStandard().getUrl() : result.getSnippet().getThumbnails().getDefault().getUrl()) : result.getSnippet().getThumbnails().getMedium().getUrl()) :
                result.getSnippet().getThumbnails().getHigh().getUrl();
    }

}
