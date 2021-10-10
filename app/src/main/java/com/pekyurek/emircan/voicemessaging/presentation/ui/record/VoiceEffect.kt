package com.pekyurek.emircan.voicemessaging.presentation.ui.record

enum class VoiceEffect(val cmd : String?) {
    NO_EFFECT(null),
    RADIO("atempo=1"),
    SQUIRREL("asetrate=22100,atempo=1/2"),
    ROBOT("asetrate=11100,atempo=4/3,atempo=1/2,atempo=3/4"),
    CAVE("aecho=0.8:0.9:1000:0.3")
}