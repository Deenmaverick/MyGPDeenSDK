package com.deenislamic.sdk.service.network.response.youtube


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class YoutubeVideoResponse(

    @SerializedName("attestation")
    var attestation: Attestation? = null,
    @SerializedName("frameworkUpdates")
    var frameworkUpdates: FrameworkUpdates? = null,
    @SerializedName("heartbeatParams")
    var heartbeatParams: HeartbeatParams? = null,
    @SerializedName("microformat")
    var microformat: Microformat? = null,
    @SerializedName("playabilityStatus")
    var playabilityStatus: PlayabilityStatus? = null,
    @SerializedName("playbackTracking")
    var playbackTracking: PlaybackTracking? = null,
    @SerializedName("playerConfig")
    var playerConfig: PlayerConfig? = null,
    @SerializedName("responseContext")
    var responseContext: ResponseContext? = null,
    @SerializedName("storyboards")
    var storyboards: Storyboards? = null,
    @SerializedName("streamingData")
    var streamingData: StreamingData? = null,
    @SerializedName("trackingParams")
    var trackingParams: String? = null,
    @SerializedName("videoDetails")
    var videoDetails: VideoDetails? = null
) {

    @Keep
    fun getUrl(qualityLabel:String): String? {
        return if(isLive()){
            getHlsVideoUrl();
        }else{
            getVideoFromAdaptiveFormatsUrl(qualityLabel)
        }
    }
    /** qualityLabel: 480p,720p,1080p*/
    @Keep
    fun getVideoFromAdaptiveFormatsUrl(qualityLabel:String): String? {
        var videoUrl:String? = null
        try {
            val obj = streamingData?.adaptiveFormats?.first {
                it?.qualityLabel?.contains(
                    qualityLabel,
                    true
                ) == true
            }
            if (obj != null) {
                videoUrl = obj.url
            } else {
                videoUrl = streamingData?.adaptiveFormats?.first()?.url
            }
        }catch (e:Exception){e.printStackTrace()}
        return videoUrl
    }

    @Keep
    fun getAudioFromAdaptiveFormatsUrl(): String? {
        var audioUrl:String? = null
        try {
            val obj = streamingData?.adaptiveFormats?.first {
                it?.mimeType?.contains("audio",true) == true
            }
            if (obj != null) {
                audioUrl = obj.url
            }
        }catch (e:Exception){e.printStackTrace()}
        return audioUrl
    }

    @Keep
    fun getVideoUrlFromFormat(qualityLabel:String): String? {
        var videoUrl:String? = null
        try {
            val obj = streamingData?.formats?.first {
                it?.qualityLabel?.contains(
                    qualityLabel,
                    true
                ) == true
            }
            if (obj != null) {
                videoUrl = obj.url
            } else {
                videoUrl = streamingData?.adaptiveFormats?.first()?.url
            }
        }catch (e:Exception){e.printStackTrace()}
        return videoUrl
    }
    @Keep
    fun getHlsVideoUrl(): String? {
       return streamingData?.hlsManifestUrl
    }
    @Keep
    fun isLive(): Boolean {
        try {
            val obj = responseContext?.serviceTrackingParams
                ?.first { it?.service.equals("GFEEDBACK", true) }
                ?.params?.first {
                    it?.key.equals("is_viewed_live", true) && it?.value.equals(
                        "True",
                        true
                    )
                }
            return obj !=null

        }catch (e:Exception){}
        return false
    }

    @Keep
    data class Attestation(
        @SerializedName("playerAttestationRenderer")
        var playerAttestationRenderer: PlayerAttestationRenderer? = null
    ) {

        @Keep
        data class PlayerAttestationRenderer(
            @SerializedName("botguardData")
            var botguardData: BotguardData? = null,
            @SerializedName("challenge")
            var challenge: String? = null
        ) {
            @Keep
            data class BotguardData(
                @SerializedName("interpreterSafeUrl")
                var interpreterSafeUrl: InterpreterSafeUrl? = null,
                @SerializedName("program")
                var program: String? = null
            ) {
                @Keep
                data class InterpreterSafeUrl(
                    @SerializedName("privateDoNotAccessOrElseTrustedResourceUrlWrappedValue")
                    var privateDoNotAccessOrElseTrustedResourceUrlWrappedValue: String? = null
                )
            }
        }
    }


    @Keep
    data class FrameworkUpdates(
        @SerializedName("entityBatchUpdate")
        var entityBatchUpdate: EntityBatchUpdate? = null
    ) {
        @Keep
        data class EntityBatchUpdate(
            @SerializedName("mutations")
            var mutations: List<Mutation?>? = null,
            @SerializedName("timestamp")
            var timestamp: Timestamp? = null
        ) {
            @Keep
            data class Mutation(
                @SerializedName("entityKey")
                var entityKey: String? = null,
                @SerializedName("payload")
                var payload: Payload? = null,
                @SerializedName("type")
                var type: String? = null
            ) {
                @Keep
                data class Payload(
                    @SerializedName("offlineabilityEntity")
                    var offlineabilityEntity: OfflineabilityEntity? = null
                ) {
                    @Keep
                    data class OfflineabilityEntity(
                        @SerializedName("accessState")
                        var accessState: String? = null,
                        @SerializedName("addToOfflineButtonState")
                        var addToOfflineButtonState: String? = null,
                        @SerializedName("key")
                        var key: String? = null
                    )
                }
            }

            @Keep
            data class Timestamp(
                @SerializedName("nanos")
                var nanos: Int? = null,
                @SerializedName("seconds")
                var seconds: String? = null
            )
        }
    }

    @Keep
    data class HeartbeatParams(
        @SerializedName("heartbeatServerData")
        var heartbeatServerData: String? = null,
        @SerializedName("intervalMilliseconds")
        var intervalMilliseconds: String? = null,
        @SerializedName("softFailOnError")
        var softFailOnError: Boolean? = null
    )

    @Keep
    data class Microformat(
        @SerializedName("playerMicroformatRenderer")
        var playerMicroformatRenderer: PlayerMicroformatRenderer? = null
    ) {
        @Keep
        data class PlayerMicroformatRenderer(
            @SerializedName("availableCountries")
            var availableCountries: List<String?>? = null,
            @SerializedName("category")
            var category: String? = null,
            @SerializedName("description")
            var description: Description? = null,
            @SerializedName("embed")
            var embed: Embed? = null,
            @SerializedName("externalChannelId")
            var externalChannelId: String? = null,
            @SerializedName("hasYpcMetadata")
            var hasYpcMetadata: Boolean? = null,
            @SerializedName("isFamilySafe")
            var isFamilySafe: Boolean? = null,
            @SerializedName("isUnlisted")
            var isUnlisted: Boolean? = null,
            @SerializedName("lengthSeconds")
            var lengthSeconds: String? = null,
            @SerializedName("liveBroadcastDetails")
            var liveBroadcastDetails: LiveBroadcastDetails? = null,
            @SerializedName("ownerChannelName")
            var ownerChannelName: String? = null,
            @SerializedName("ownerProfileUrl")
            var ownerProfileUrl: String? = null,
            @SerializedName("publishDate")
            var publishDate: String? = null,
            @SerializedName("thumbnail")
            var thumbnail: Thumbnail? = null,
            @SerializedName("title")
            var title: Title? = null,
            @SerializedName("uploadDate")
            var uploadDate: String? = null,
            @SerializedName("viewCount")
            var viewCount: String? = null
        ) {
            @Keep
            data class Description(
                @SerializedName("simpleText")
                var simpleText: String? = null
            )

            @Keep
            data class Embed(
                @SerializedName("flashSecureUrl")
                var flashSecureUrl: String? = null,
                @SerializedName("flashUrl")
                var flashUrl: String? = null,
                @SerializedName("height")
                var height: Int? = null,
                @SerializedName("iframeUrl")
                var iframeUrl: String? = null,
                @SerializedName("width")
                var width: Int? = null
            )

            @Keep
            data class LiveBroadcastDetails(
                @SerializedName("isLiveNow")
                var isLiveNow: Boolean? = null,
                @SerializedName("startTimestamp")
                var startTimestamp: String? = null
            )

            @Keep
            data class Thumbnail(
                @SerializedName("thumbnails")
                var thumbnails: List<Thumbnail?>? = null
            ) {
                @Keep
                data class Thumbnail(
                    @SerializedName("height")
                    var height: Int? = null,
                    @SerializedName("url")
                    var url: String? = null,
                    @SerializedName("width")
                    var width: Int? = null
                )
            }

            @Keep
            data class Title(
                @SerializedName("simpleText")
                var simpleText: String? = null
            )
        }
    }

    @Keep
    data class PlayabilityStatus(
        @SerializedName("contextParams")
        var contextParams: String? = null,
        @SerializedName("liveStreamability")
        var liveStreamability: LiveStreamability? = null,
        @SerializedName("miniplayer")
        var miniplayer: Miniplayer? = null,
        @SerializedName("playableInEmbed")
        var playableInEmbed: Boolean? = null,
        @SerializedName("status")
        var status: String? = null
    ) {
        @Keep
        data class LiveStreamability(
            @SerializedName("liveStreamabilityRenderer")
            var liveStreamabilityRenderer: LiveStreamabilityRenderer? = null
        ) {
            @Keep
            data class LiveStreamabilityRenderer(
                @SerializedName("broadcastId")
                var broadcastId: String? = null,
                @SerializedName("pollDelayMs")
                var pollDelayMs: String? = null,
                @SerializedName("videoId")
                var videoId: String? = null
            )
        }

        @Keep
        data class Miniplayer(
            @SerializedName("miniplayerRenderer")
            var miniplayerRenderer: MiniplayerRenderer? = null
        ) {
            @Keep
            data class MiniplayerRenderer(
                @SerializedName("playbackMode")
                var playbackMode: String? = null
            )
        }
    }

    @Keep
    data class PlaybackTracking(
        @SerializedName("atrUrl")
        var atrUrl: AtrUrl? = null,
        @SerializedName("ptrackingUrl")
        var ptrackingUrl: PtrackingUrl? = null,
        @SerializedName("qoeUrl")
        var qoeUrl: QoeUrl? = null,
        @SerializedName("videostatsDefaultFlushIntervalSeconds")
        var videostatsDefaultFlushIntervalSeconds: Int? = null,
        @SerializedName("videostatsDelayplayUrl")
        var videostatsDelayplayUrl: VideostatsDelayplayUrl? = null,
        @SerializedName("videostatsPlaybackUrl")
        var videostatsPlaybackUrl: VideostatsPlaybackUrl? = null,
        @SerializedName("videostatsScheduledFlushWalltimeSeconds")
        var videostatsScheduledFlushWalltimeSeconds: List<Int?>? = null,
        @SerializedName("videostatsWatchtimeUrl")
        var videostatsWatchtimeUrl: VideostatsWatchtimeUrl? = null
    ) {
        @Keep
        data class AtrUrl(
            @SerializedName("baseUrl")
            var baseUrl: String? = null,
            @SerializedName("elapsedMediaTimeSeconds")
            var elapsedMediaTimeSeconds: Int? = null
        )

        @Keep
        data class PtrackingUrl(
            @SerializedName("baseUrl")
            var baseUrl: String? = null
        )

        @Keep
        data class QoeUrl(
            @SerializedName("baseUrl")
            var baseUrl: String? = null
        )

        @Keep
        data class VideostatsDelayplayUrl(
            @SerializedName("baseUrl")
            var baseUrl: String? = null,
            @SerializedName("elapsedMediaTimeSeconds")
            var elapsedMediaTimeSeconds: Int? = null
        )

        @Keep
        data class VideostatsPlaybackUrl(
            @SerializedName("baseUrl")
            var baseUrl: String? = null
        )

        @Keep
        data class VideostatsWatchtimeUrl(
            @SerializedName("baseUrl")
            var baseUrl: String? = null
        )
    }

    @Keep
    data class PlayerConfig(
        @SerializedName("audioConfig")
        var audioConfig: AudioConfig? = null,
        @SerializedName("livePlayerConfig")
        var livePlayerConfig: LivePlayerConfig? = null,
        @SerializedName("mediaCommonConfig")
        var mediaCommonConfig: MediaCommonConfig? = null,
        @SerializedName("webPlayerConfig")
        var webPlayerConfig: WebPlayerConfig? = null
    ) {
        @Keep
        data class AudioConfig(
            @SerializedName("enablePerFormatLoudness")
            var enablePerFormatLoudness: Boolean? = null
        )

        @Keep
        data class LivePlayerConfig(
            @SerializedName("hasSubfragmentedFmp4")
            var hasSubfragmentedFmp4: Boolean? = null,
            @SerializedName("hasSubfragmentedWebm")
            var hasSubfragmentedWebm: Boolean? = null,
            @SerializedName("liveReadaheadSeconds")
            var liveReadaheadSeconds: Double? = null
        )

        @Keep
        data class MediaCommonConfig(
            @SerializedName("dynamicReadaheadConfig")
            var dynamicReadaheadConfig: DynamicReadaheadConfig? = null
        ) {
            @Keep
            data class DynamicReadaheadConfig(
                @SerializedName("maxReadAheadMediaTimeMs")
                var maxReadAheadMediaTimeMs: Int? = null,
                @SerializedName("minReadAheadMediaTimeMs")
                var minReadAheadMediaTimeMs: Int? = null,
                @SerializedName("readAheadGrowthRateMs")
                var readAheadGrowthRateMs: Int? = null
            )
        }

        @Keep
        data class WebPlayerConfig(
            @SerializedName("webPlayerActionsPorting")
            var webPlayerActionsPorting: WebPlayerActionsPorting? = null
        ) {
            @Keep
            data class WebPlayerActionsPorting(
                @SerializedName("addToWatchLaterCommand")
                var addToWatchLaterCommand: AddToWatchLaterCommand? = null,
                @SerializedName("getSharePanelCommand")
                var getSharePanelCommand: GetSharePanelCommand? = null,
                @SerializedName("removeFromWatchLaterCommand")
                var removeFromWatchLaterCommand: RemoveFromWatchLaterCommand? = null,
                @SerializedName("subscribeCommand")
                var subscribeCommand: SubscribeCommand? = null,
                @SerializedName("unsubscribeCommand")
                var unsubscribeCommand: UnsubscribeCommand? = null
            ) {
                @Keep
                data class AddToWatchLaterCommand(
                    @SerializedName("clickTrackingParams")
                    var clickTrackingParams: String? = null,
                    @SerializedName("commandMetadata")
                    var commandMetadata: CommandMetadata? = null,
                    @SerializedName("playlistEditEndpoint")
                    var playlistEditEndpoint: PlaylistEditEndpoint? = null
                ) {
                    @Keep
                    data class CommandMetadata(
                        @SerializedName("webCommandMetadata")
                        var webCommandMetadata: WebCommandMetadata? = null
                    ) {
                        @Keep
                        data class WebCommandMetadata(
                            @SerializedName("apiUrl")
                            var apiUrl: String? = null,
                            @SerializedName("sendPost")
                            var sendPost: Boolean? = null
                        )
                    }

                    @Keep
                    data class PlaylistEditEndpoint(
                        @SerializedName("actions")
                        var actions: List<Action?>? = null,
                        @SerializedName("playlistId")
                        var playlistId: String? = null
                    ) {

                        @Keep
                        data class Action(
                            @SerializedName("action")
                            var action: String? = null,
                            @SerializedName("addedVideoId")
                            var addedVideoId: String? = null
                        )
                    }
                }

                @Keep
                data class GetSharePanelCommand(
                    @SerializedName("clickTrackingParams")
                    var clickTrackingParams: String? = null,
                    @SerializedName("commandMetadata")
                    var commandMetadata: CommandMetadata? = null,
                    @SerializedName("webPlayerShareEntityServiceEndpoint")
                    var webPlayerShareEntityServiceEndpoint: WebPlayerShareEntityServiceEndpoint? = null
                ) {
                    @Keep
                    data class CommandMetadata(
                        @SerializedName("webCommandMetadata")
                        var webCommandMetadata: WebCommandMetadata? = null
                    ) {
                        @Keep
                        data class WebCommandMetadata(
                            @SerializedName("apiUrl")
                            var apiUrl: String? = null,
                            @SerializedName("sendPost")
                            var sendPost: Boolean? = null
                        )
                    }

                    @Keep
                    data class WebPlayerShareEntityServiceEndpoint(
                        @SerializedName("serializedShareEntity")
                        var serializedShareEntity: String? = null
                    )
                }

                @Keep
                data class RemoveFromWatchLaterCommand(
                    @SerializedName("clickTrackingParams")
                    var clickTrackingParams: String? = null,
                    @SerializedName("commandMetadata")
                    var commandMetadata: CommandMetadata? = null,
                    @SerializedName("playlistEditEndpoint")
                    var playlistEditEndpoint: PlaylistEditEndpoint? = null
                ) {
                    @Keep
                    data class CommandMetadata(
                        @SerializedName("webCommandMetadata")
                        var webCommandMetadata: WebCommandMetadata? = null
                    ) {
                        @Keep
                        data class WebCommandMetadata(
                            @SerializedName("apiUrl")
                            var apiUrl: String? = null,
                            @SerializedName("sendPost")
                            var sendPost: Boolean? = null
                        )
                    }

                    @Keep
                    data class PlaylistEditEndpoint(
                        @SerializedName("actions")
                        var actions: List<Action?>? = null,
                        @SerializedName("playlistId")
                        var playlistId: String? = null
                    ) {
                        @Keep
                        data class Action(
                            @SerializedName("action")
                            var action: String? = null,
                            @SerializedName("removedVideoId")
                            var removedVideoId: String? = null
                        )
                    }
                }

                @Keep
                data class SubscribeCommand(
                    @SerializedName("clickTrackingParams")
                    var clickTrackingParams: String? = null,
                    @SerializedName("commandMetadata")
                    var commandMetadata: CommandMetadata? = null,
                    @SerializedName("subscribeEndpoint")
                    var subscribeEndpoint: SubscribeEndpoint? = null
                ) {
                    @Keep
                    data class CommandMetadata(
                        @SerializedName("webCommandMetadata")
                        var webCommandMetadata: WebCommandMetadata? = null
                    ) {
                        @Keep
                        data class WebCommandMetadata(
                            @SerializedName("apiUrl")
                            var apiUrl: String? = null,
                            @SerializedName("sendPost")
                            var sendPost: Boolean? = null
                        )
                    }

                    @Keep
                    data class SubscribeEndpoint(
                        @SerializedName("channelIds")
                        var channelIds: List<String?>? = null,
                        @SerializedName("params")
                        var params: String? = null
                    )
                }

                @Keep
                data class UnsubscribeCommand(
                    @SerializedName("clickTrackingParams")
                    var clickTrackingParams: String? = null,
                    @SerializedName("commandMetadata")
                    var commandMetadata: CommandMetadata? = null,
                    @SerializedName("unsubscribeEndpoint")
                    var unsubscribeEndpoint: UnsubscribeEndpoint? = null
                ) {
                    @Keep
                    data class CommandMetadata(
                        @SerializedName("webCommandMetadata")
                        var webCommandMetadata: WebCommandMetadata? = null
                    ) {
                        @Keep
                        data class WebCommandMetadata(
                            @SerializedName("apiUrl")
                            var apiUrl: String? = null,
                            @SerializedName("sendPost")
                            var sendPost: Boolean? = null
                        )
                    }

                    @Keep
                    data class UnsubscribeEndpoint(
                        @SerializedName("channelIds")
                        var channelIds: List<String?>? = null,
                        @SerializedName("params")
                        var params: String? = null
                    )
                }
            }
        }
    }

    @Keep
    data class ResponseContext(
        @SerializedName("mainAppWebResponseContext")
        var mainAppWebResponseContext: MainAppWebResponseContext? = null,
        @SerializedName("serviceTrackingParams")
        var serviceTrackingParams: List<ServiceTrackingParam?>? = null,
        @SerializedName("visitorData")
        var visitorData: String? = null,
        @SerializedName("webResponseContextExtensionData")
        var webResponseContextExtensionData: WebResponseContextExtensionData? = null
    ) {
        @Keep
        data class MainAppWebResponseContext(
            @SerializedName("loggedOut")
            var loggedOut: Boolean? = null
        )

        @Keep
        data class ServiceTrackingParam(
            @SerializedName("params")
            var params: List<Param?>? = null,
            @SerializedName("service")
            var service: String? = null
        ) {
            @Keep
            data class Param(
                @SerializedName("key")
                var key: String? = null,
                @SerializedName("value")
                var value: String? = null
            )
        }

        @Keep
        data class WebResponseContextExtensionData(
            @SerializedName("hasDecorated")
            var hasDecorated: Boolean? = null
        )
    }

    @Keep
    data class Storyboards(
        @SerializedName("playerLiveStoryboardSpecRenderer")
        var playerLiveStoryboardSpecRenderer: PlayerLiveStoryboardSpecRenderer? = null
    ) {
        @Keep
        data class PlayerLiveStoryboardSpecRenderer(
            @SerializedName("spec")
            var spec: String? = null
        )
    }

    @Keep
    data class StreamingData(
        @SerializedName("adaptiveFormats")
        var adaptiveFormats: List<AdaptiveFormat?>? = null,

        @SerializedName("formats")
        var formats: List<Format?>? = null,

        @SerializedName("dashManifestUrl")
        var dashManifestUrl: String? = null,
        @SerializedName("expiresInSeconds")
        var expiresInSeconds: String? = null,
        @SerializedName("hlsManifestUrl")
        var hlsManifestUrl: String? = null
    ) {

        @Keep
        data class Format(
            @SerializedName("approxDurationMs")
            val approxDurationMs: String?,
            @SerializedName("audioChannels")
            val audioChannels: Int?,
            @SerializedName("audioQuality")
            val audioQuality: String?,
            @SerializedName("audioSampleRate")
            val audioSampleRate: String?,
            @SerializedName("bitrate")
            val bitrate: Int?,
            @SerializedName("fps")
            val fps: Int?,
            @SerializedName("height")
            val height: Int?,
            @SerializedName("itag")
            val itag: Int?,
            @SerializedName("lastModified")
            val lastModified: String?,
            @SerializedName("mimeType")
            val mimeType: String?,
            @SerializedName("projectionType")
            val projectionType: String?,
            @SerializedName("quality")
            val quality: String?,
            @SerializedName("qualityLabel")
            val qualityLabel: String?,
            @SerializedName("url")
            val url: String?,
            @SerializedName("width")
            val width: Int?
        )

        @Keep
        data class AdaptiveFormat(
            @SerializedName("audioChannels")
            var audioChannels: Int? = null,
            @SerializedName("audioQuality")
            var audioQuality: String? = null,
            @SerializedName("audioSampleRate")
            var audioSampleRate: String? = null,
            @SerializedName("bitrate")
            var bitrate: Int? = null,
            @SerializedName("fps")
            var fps: Int? = null,
            @SerializedName("height")
            var height: Int? = null,
            @SerializedName("highReplication")
            var highReplication: Boolean? = null,
            @SerializedName("itag")
            var itag: Int? = null,
            @SerializedName("lastModified")
            var lastModified: String? = null,
            @SerializedName("maxDvrDurationSec")
            var maxDvrDurationSec: Int? = null,
            @SerializedName("mimeType")
            var mimeType: String? = null,
            @SerializedName("projectionType")
            var projectionType: String? = null,
            @SerializedName("quality")
            var quality: String? = null,
            @SerializedName("qualityLabel")
            var qualityLabel: String? = null,
            @SerializedName("targetDurationSec")
            var targetDurationSec: Int? = null,
            @SerializedName("url")
            var url: String? = null,
            @SerializedName("width")
            var width: Int? = null
        )
    }

    @Keep
    data class VideoDetails(
        @SerializedName("allowRatings")
        var allowRatings: Boolean? = null,
        @SerializedName("author")
        var author: String? = null,
        @SerializedName("averageRating")
        var averageRating: Double? = null,
        @SerializedName("channelId")
        var channelId: String? = null,
        @SerializedName("isCrawlable")
        var isCrawlable: Boolean? = null,
        @SerializedName("isLive")
        var isLive: Boolean? = null,
        @SerializedName("isLiveContent")
        var isLiveContent: Boolean? = null,
        @SerializedName("isLiveDvrEnabled")
        var isLiveDvrEnabled: Boolean? = null,
        @SerializedName("isLowLatencyLiveStream")
        var isLowLatencyLiveStream: Boolean? = null,
        @SerializedName("isOwnerViewing")
        var isOwnerViewing: Boolean? = null,
        @SerializedName("isPrivate")
        var isPrivate: Boolean? = null,
        @SerializedName("isUnpluggedCorpus")
        var isUnpluggedCorpus: Boolean? = null,
        @SerializedName("keywords")
        var keywords: List<String?>? = null,
        @SerializedName("latencyClass")
        var latencyClass: String? = null,
        @SerializedName("lengthSeconds")
        var lengthSeconds: String? = null,
        @SerializedName("liveChunkReadahead")
        var liveChunkReadahead: Int? = null,
        @SerializedName("shortDescription")
        var shortDescription: String? = null,
        @SerializedName("thumbnail")
        var thumbnail: Thumbnail? = null,
        @SerializedName("title")
        var title: String? = null,
        @SerializedName("videoId")
        var videoId: String? = null,
        @SerializedName("viewCount")
        var viewCount: String? = null
    ) {
        @Keep
        data class Thumbnail(
            @SerializedName("thumbnails")
            var thumbnails: List<Thumbnail?>? = null
        ) {
            @Keep
            data class Thumbnail(
                @SerializedName("height")
                var height: Int? = null,
                @SerializedName("url")
                var url: String? = null,
                @SerializedName("width")
                var width: Int? = null
            )
        }
    }



}