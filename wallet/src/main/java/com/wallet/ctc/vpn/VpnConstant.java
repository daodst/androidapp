package com.wallet.ctc.vpn;

public class VpnConstant {
    public static final String default_config = "{\n" +
            "    \"outbounds\": [\n" +
            "        {\n" +
            "            \"protocol\": \"socks\",\n" +
            "            \"settings\": {\n" +
            "                \"servers\": [\n" +
            "                    {\n" +
            "                        \"address\": \"3.35.42.193\",\n" +
            "                        \"port\": 8128,\n" +
            "                        \"users\": [\n" +
            "                            {\n" +
            "                                \"user\": \"xs\",\n" +
            "                                \"pass\": \"xs123.\"\n" +
            "                            }\n" +
            "                        ]\n" +
            "                    }\n" +
            "                ]\n" +
            "            },\n" +
            "            \"streamSettings\": {\n" +
            "                \"network\": \"tcp\"\n" +
            "            },\n" +
            "            \"tag\": \"proxy\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"protocol\": \"freedom\",\n" +
            "            \"settings\": {\n" +
            "                \"domainStrategy\": \"UseIP\"\n" +
            "            },\n" +
            "            \"streamSettings\": {},\n" +
            "            \"tag\": \"direct\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"protocol\": \"blackhole\",\n" +
            "            \"settings\": {},\n" +
            "            \"tag\": \"block\"\n" +
            "        },\n" +
            "\t{\n" +
            "\t    \"protocol\": \"dns\",\n" +
            "\t    \"tag\": \"dns-out\"\n" +
            "\t}\n" +
            "    ],\n" +
            "    \"dns\": {\n" +
            "        \"clientIp\": \"115.239.211.92\",\n" +
            "        \"hosts\": {\n" +
            "            \"localhost\": \"127.0.0.1\"\n" +
            "        },\n" +
            "        \"servers\": [\n" +
            "            \"114.114.114.114\",\n" +
            "            {\n" +
            "                \"address\": \"8.8.8.8\",\n" +
            "                \"domains\": [\n" +
            "                    \"google\",\n" +
            "                    \"android\",\n" +
            "                    \"fbcdn\",\n" +
            "                    \"facebook\",\n" +
            "                    \"domain:fb.com\",\n" +
            "                    \"instagram\",\n" +
            "                    \"whatsapp\",\n" +
            "                    \"akamai\",\n" +
            "                    \"domain:line-scdn.net\",\n" +
            "                    \"domain:line.me\",\n" +
            "                    \"domain:naver.jp\"\n" +
            "                ],\n" +
            "                \"port\": 53\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    \"log\": {\n" +
            "        \"loglevel\": \"warning\"\n" +
            "    },\n" +
            "    \"policy\": {\n" +
            "        \"levels\": {\n" +
            "            \"0\": {\n" +
            "                \"bufferSize\": 4096,\n" +
            "                \"connIdle\": 30,\n" +
            "                \"downlinkOnly\": 0,\n" +
            "                \"handshake\": 4,\n" +
            "                \"uplinkOnly\": 0\n" +
            "            }\n" +
            "        }\n" +
            "    },\n" +
            "    \"routing\": {\n" +
            "        \"domainStrategy\": \"IPIfNonMatch\",\n" +
            "        \"rules\": [\n" +
            "            {\n" +
            "\t\t\t\t\"inboundTag\": [\"tun2socks\"],\n" +
            "                \"network\": \"udp\",\n" +
            "                \"port\": 53,\n" +
            "                \"outboundTag\": \"dns-out\",\n" +
            "                \"type\": \"field\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"domain\": [\n" +
            "                    \"domain:setup.icloud.com\"\n" +
            "                ],\n" +
            "                \"outboundTag\": \"proxy\",\n" +
            "                \"type\": \"field\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"ip\": [\n" +
            "                    \"8.8.8.8/32\",\n" +
            "                    \"8.8.4.4/32\",\n" +
            "                    \"1.1.1.1/32\",\n" +
            "                    \"1.0.0.1/32\",\n" +
            "                    \"9.9.9.9/32\",\n" +
            "                    \"149.112.112.112/32\",\n" +
            "                    \"208.67.222.222/32\",\n" +
            "                    \"208.67.220.220/32\"\n" +
            "                ],\n" +
            "                \"outboundTag\": \"proxy\",\n" +
            "                \"type\": \"field\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"ip\": [\n" +
            "                    \"geoip:cn\",\n" +
            "                    \"geoip:private\"\n" +
            "                ],\n" +
            "                \"outboundTag\": \"direct\",\n" +
            "                \"type\": \"field\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"outboundTag\": \"direct\",\n" +
            "                \"port\": \"123\",\n" +
            "                \"type\": \"field\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"domain\": [\n" +
            "                    \"domain:pstatp.com\",\n" +
            "                    \"domain:snssdk.com\",\n" +
            "                    \"domain:toutiao.com\",\n" +
            "                    \"domain:ixigua.com\",\n" +
            "                    \"domain:apple.com\",\n" +
            "                    \"domain:crashlytics.com\",\n" +
            "                    \"domain:icloud.com\",\n" +
            "                    \"cctv\",\n" +
            "                    \"umeng\",\n" +
            "                    \"domain:weico.cc\",\n" +
            "                    \"domain:jd.com\",\n" +
            "                    \"domain:360buy.com\",\n" +
            "                    \"domain:360buyimg.com\",\n" +
            "                    \"domain:douyu.tv\",\n" +
            "                    \"domain:douyu.com\",\n" +
            "                    \"domain:douyucdn.cn\",\n" +
            "                    \"geosite:cn\"\n" +
            "                ],\n" +
            "                \"outboundTag\": \"direct\",\n" +
            "                \"type\": \"field\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"ip\": [\n" +
            "                    \"149.154.167.0/24\",\n" +
            "                    \"149.154.175.0/24\",\n" +
            "                    \"91.108.56.0/24\",\n" +
            "                    \"125.209.222.0/24\"\n" +
            "                ],\n" +
            "                \"outboundTag\": \"proxy\",\n" +
            "                \"type\": \"field\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"domain\": [\n" +
            "                    \"twitter\",\n" +
            "                    \"domain:twimg.com\",\n" +
            "                    \"domain:t.co\",\n" +
            "                    \"google\",\n" +
            "                    \"domain:ggpht.com\",\n" +
            "                    \"domain:gstatic.com\",\n" +
            "                    \"domain:youtube.com\",\n" +
            "                    \"domain:ytimg.com\",\n" +
            "                    \"pixiv\",\n" +
            "                    \"domain:pximg.net\",\n" +
            "                    \"tumblr\",\n" +
            "                    \"instagram\",\n" +
            "                    \"domain:line-scdn.net\",\n" +
            "                    \"domain:line.me\",\n" +
            "                    \"domain:naver.jp\",\n" +
            "                    \"domain:facebook.com\",\n" +
            "                    \"domain:fbcdn.net\",\n" +
            "                    \"pinterest\",\n" +
            "                    \"github\",\n" +
            "                    \"dropbox\",\n" +
            "                    \"netflix\",\n" +
            "                    \"domain:medium.com\",\n" +
            "                    \"domain:fivecdm.com\"\n" +
            "                ],\n" +
            "                \"outboundTag\": \"proxy\",\n" +
            "                \"type\": \"field\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"strategy\": \"rules\"\n" +
            "    }\n" +
            "}";
}
