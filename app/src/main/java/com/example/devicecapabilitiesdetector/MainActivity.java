package com.example.devicecapabilitiesdetector;

import android.app.Activity;
import android.media.MediaCodecInfo;
import android.media.MediaCodecInfo.CodecProfileLevel;
import android.media.MediaCodecList;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class MainActivity extends Activity {
    private static final String TAG = "DeviceCapabilitiesDetector";
    private static final boolean debug = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "isHdr10PlusSupported: " + (isHdr10PlusSupported() ? "yes" : "no"));
    }

    private static boolean isHdr10PlusSupported() {
        final List<Integer> acceptedProfiles = new ArrayList<>();
        acceptedProfiles.add(CodecProfileLevel.VP9Profile2HDR10Plus);
        acceptedProfiles.add(CodecProfileLevel.VP9Profile3HDR10Plus);
        acceptedProfiles.add(CodecProfileLevel.HEVCProfileMain10HDR10Plus);
        acceptedProfiles.add(CodecProfileLevel.AV1ProfileMain10HDR10Plus);
        if (debug) {
            boolean found = false;
            MediaCodecList codecList = new MediaCodecList(MediaCodecList.REGULAR_CODECS);
            for (MediaCodecInfo codecInfo : codecList.getCodecInfos()) {
                for (String supportedType : codecInfo.getSupportedTypes()) {
                    MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(supportedType);
                    for (CodecProfileLevel level : capabilities.profileLevels) {
                        if (acceptedProfiles.contains(level.profile)) {
                            Log.d(TAG, "Returning true for codec=" + codecInfo.getName() + " level=" + level.profile);
                            found = true;
                        }
                    }
                }
            }
            return found;
        }
        return anyCodecCapabilitiesMatch(codec -> Arrays.stream(codec.profileLevels)
                .map(level -> level.profile)
                .anyMatch(acceptedProfiles::contains));
    }

    private static boolean anyCodecCapabilitiesMatch(Predicate<MediaCodecInfo.CodecCapabilities> predicate) {
        MediaCodecList codecList = new MediaCodecList(MediaCodecList.ALL_CODECS);
        for (MediaCodecInfo codecInfo : codecList.getCodecInfos()) {
            for (String supportedType : codecInfo.getSupportedTypes()) {
                MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(supportedType);
                if (predicate.test(capabilities)) {
                    return true;
                }
            }
        }
        return false;
    }
}