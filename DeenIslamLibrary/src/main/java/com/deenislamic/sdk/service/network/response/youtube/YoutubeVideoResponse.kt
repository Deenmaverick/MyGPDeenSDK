package com.deenislamic.sdk.service.network.response.youtube

import androidx.annotation.Keep


@Keep
internal data class YoutubeVideoResponse(

    var attestation: Attestation? = null,
    var frameworkUpdates: FrameworkUpdates? = null,
    var heartbeatParams: HeartbeatParams? = null,
    var microformat: Microformat? = null,
    var playabilityStatus: PlayabilityStatus? = null,
    var playbackTracking: PlaybackTracking? = null,
    var playerConfig: PlayerConfig? = null,
    var responseContext: ResponseContext? = null,
    var storyboards: Storyboards? = null,
    var streamingData: StreamingData? = null,
    var trackingParams: String? = null,
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
        var playerAttestationRenderer: PlayerAttestationRenderer? = null
    ) {

        @Keep
        data class PlayerAttestationRenderer(
            var botguardData: BotguardData? = null,
            var challenge: String? = null
        ) {
            @Keep
            data class BotguardData(
                var interpreterSafeUrl: InterpreterSafeUrl? = null,
                var program: String? = null
            ) {
                @Keep
                data class InterpreterSafeUrl(
                    var privateDoNotAccessOrElseTrustedResourceUrlWrappedValue: String? = null
                )
            }
        }
    }


    @Keep
    data class FrameworkUpdates(
        var entityBatchUpdate: EntityBatchUpdate? = null
    ) {
        @Keep
        data class EntityBatchUpdate(
            var mutations: List<Mutation?>? = null,
            var timestamp: Timestamp? = null
        ) {
            @Keep
            data class Mutation(
                var entityKey: String? = null,
                var payload: Payload? = null,
                var type: String? = null
            ) {
                @Keep
                data class Payload(
                    var offlineabilityEntity: OfflineabilityEntity? = null
                ) {
                    @Keep
                    data class OfflineabilityEntity(
                        var accessState: String? = null,
                        var addToOfflineButtonState: String? = null,
                        var key: String? = null
                    )
                }
            }

            @Keep
            data class Timestamp(
                var nanos: Int? = null,
                var seconds: String? = null
            )
        }
    }

    @Keep
    data class HeartbeatParams(
        var heartbeatServerData: String? = null,
        var intervalMilliseconds: String? = null,
        var softFailOnError: Boolean? = null
    )

    @Keep
    data class Microformat(
        var playerMicroformatRenderer: PlayerMicroformatRenderer? = null
    ) {
        @Keep
        data class PlayerMicroformatRenderer(
            var availableCountries: List<String?>? = null,
            var category: String? = null,
            var description: Description? = null,
            var embed: Embed? = null,
            var externalChannelId: String? = null,
            var hasYpcMetadata: Boolean? = null,
            var isFamilySafe: Boolean? = null,
            var isUnlisted: Boolean? = null,
            var lengthSeconds: String? = null,
            var liveBroadcastDetails: LiveBroadcastDetails? = null,
            var ownerChannelName: String? = null,
            var ownerProfileUrl: String? = null,
            var publishDate: String? = null,
            var thumbnail: Thumbnail? = null,
            var title: Title? = null,
            var uploadDate: String? = null,
            var viewCount: String? = null

        ) {
            @Keep
            data class Description(
                var simpleText: String? = null
            )

            @Keep
            data class Embed(
                var flashSecureUrl: String? = null,
                var flashUrl: String? = null,
                var height: Int? = null,
                var iframeUrl: String? = null,
                var width: Int? = null
            )

            @Keep
            data class LiveBroadcastDetails(
                var isLiveNow: Boolean? = null,
                var startTimestamp: String? = null
            )

            @Keep
            data class Thumbnail(
                var thumbnails: List<Thumbnail?>? = null
            ) {
                @Keep
                data class Thumbnail(
                    var height: Int? = null,
                    var url: String? = null,
                    var width: Int? = null
                )
            }

            @Keep
            data class Title(
                var simpleText: String? = null
            )
        }
    }

    @Keep
    data class PlayabilityStatus(
        var contextParams: String? = null,
        var liveStreamability: LiveStreamability? = null,
        var miniplayer: Miniplayer? = null,
        var playableInEmbed: Boolean? = null,
        var status: String? = null

    ) {
        @Keep
        data class LiveStreamability(
            var liveStreamabilityRenderer: LiveStreamabilityRenderer? = null
        ) {
            @Keep
            data class LiveStreamabilityRenderer(
                var broadcastId: String? = null,
                var pollDelayMs: String? = null,
                var videoId: String? = null

            )
        }

        @Keep
        data class Miniplayer(
            var miniplayerRenderer: MiniplayerRenderer? = null
        ) {
            @Keep
            data class MiniplayerRenderer(
                var playbackMode: String? = null
            )
        }
    }

    @Keep
    data class PlaybackTracking(
        var atrUrl: AtrUrl? = null,
        var ptrackingUrl: PtrackingUrl? = null,
        var qoeUrl: QoeUrl? = null,
        var videostatsDefaultFlushIntervalSeconds: Int? = null,
        var videostatsDelayplayUrl: VideostatsDelayplayUrl? = null,
        var videostatsPlaybackUrl: VideostatsPlaybackUrl? = null,
        var videostatsScheduledFlushWalltimeSeconds: List<Int?>? = null,
        var videostatsWatchtimeUrl: VideostatsWatchtimeUrl? = null

    ) {
        @Keep
        data class AtrUrl(
            var baseUrl: String? = null,
            var elapsedMediaTimeSeconds: Int? = null
        )

        @Keep
        data class PtrackingUrl(
            var baseUrl: String? = null
        )

        @Keep
        data class QoeUrl(
            var baseUrl: String? = null
        )

        @Keep
        data class VideostatsDelayplayUrl(
            var baseUrl: String? = null,
            var elapsedMediaTimeSeconds: Int? = null
        )

        @Keep
        data class VideostatsPlaybackUrl(
            var baseUrl: String? = null
        )

        @Keep
        data class VideostatsWatchtimeUrl(
            var baseUrl: String? = null
        )
    }

    @Keep
    data class PlayerConfig(
        var audioConfig: AudioConfig? = null,
        var livePlayerConfig: LivePlayerConfig? = null,
        var mediaCommonConfig: MediaCommonConfig? = null,
        var webPlayerConfig: WebPlayerConfig? = null

    ) {
        @Keep
        data class AudioConfig(
            var enablePerFormatLoudness: Boolean? = null
        )

        @Keep
        data class LivePlayerConfig(
            var hasSubfragmentedFmp4: Boolean? = null,
            var hasSubfragmentedWebm: Boolean? = null,
            var liveReadaheadSeconds: Double? = null
        )

        @Keep
        data class MediaCommonConfig(
            var dynamicReadaheadConfig: DynamicReadaheadConfig? = null
        ) {
            @Keep
            data class DynamicReadaheadConfig(
                var maxReadAheadMediaTimeMs: Int? = null,
                var minReadAheadMediaTimeMs: Int? = null,
                var readAheadGrowthRateMs: Int? = null
            )
        }

        @Keep
        data class WebPlayerConfig(
            var webPlayerActionsPorting: WebPlayerActionsPorting? = null
        ) {
            @Keep
            data class WebPlayerActionsPorting(
                var addToWatchLaterCommand: AddToWatchLaterCommand? = null,
                var getSharePanelCommand: GetSharePanelCommand? = null,
                var removeFromWatchLaterCommand: RemoveFromWatchLaterCommand? = null,
                var subscribeCommand: SubscribeCommand? = null,
                var unsubscribeCommand: UnsubscribeCommand? = null

            ) {
                @Keep
                data class AddToWatchLaterCommand(
                    var clickTrackingParams: String? = null,
                    var commandMetadata: CommandMetadata? = null,
                    var playlistEditEndpoint: PlaylistEditEndpoint? = null
                ) {
                    @Keep
                    data class CommandMetadata(
                        var webCommandMetadata: WebCommandMetadata? = null
                    ) {
                        @Keep
                        data class WebCommandMetadata(
                            var apiUrl: String? = null,
                            var sendPost: Boolean? = null
                        )
                    }

                    @Keep
                    data class PlaylistEditEndpoint(
                        var actions: List<Action?>? = null,
                        var playlistId: String? = null
                    ) {

                        @Keep
                        data class Action(
                            var action: String? = null,
                            var addedVideoId: String? = null
                        )
                    }
                }

                @Keep
                data class GetSharePanelCommand(
                    var clickTrackingParams: String? = null,
                    var commandMetadata: CommandMetadata? = null,
                    var webPlayerShareEntityServiceEndpoint: WebPlayerShareEntityServiceEndpoint? = null
                ) {
                    @Keep
                    data class CommandMetadata(
                        var webCommandMetadata: WebCommandMetadata? = null
                    ) {
                        @Keep
                        data class WebCommandMetadata(
                            var apiUrl: String? = null,
                            var sendPost: Boolean? = null
                        )
                    }

                    @Keep
                    data class WebPlayerShareEntityServiceEndpoint(
                        var serializedShareEntity: String? = null
                    )
                }

                @Keep
                data class RemoveFromWatchLaterCommand(
                    var clickTrackingParams: String? = null,
                    var commandMetadata: CommandMetadata? = null,
                    var playlistEditEndpoint: PlaylistEditEndpoint? = null
                ) {
                    @Keep
                    data class CommandMetadata(
                        var webCommandMetadata: WebCommandMetadata? = null
                    ) {
                        @Keep
                        data class WebCommandMetadata(
                            var apiUrl: String? = null,
                            var sendPost: Boolean? = null
                        )
                    }

                    @Keep
                    data class PlaylistEditEndpoint(
                        var actions: List<Action?>? = null,
                        var playlistId: String? = null
                    ) {
                        @Keep
                        data class Action(
                            var action: String? = null,
                            var removedVideoId: String? = null
                        )
                    }
                }

                @Keep
                data class SubscribeCommand(
                    var clickTrackingParams: String? = null,
                    var commandMetadata: CommandMetadata? = null,
                    var subscribeEndpoint: SubscribeEndpoint? = null
                ) {
                    @Keep
                    data class CommandMetadata(
                        var webCommandMetadata: WebCommandMetadata? = null
                    ) {
                        @Keep
                        data class WebCommandMetadata(
                            var apiUrl: String? = null,
                            var sendPost: Boolean? = null
                        )
                    }

                    @Keep
                    data class SubscribeEndpoint(
                        var channelIds: List<String?>? = null,
                        var params: String? = null
                    )
                }

                @Keep
                data class UnsubscribeCommand(
                    var clickTrackingParams: String? = null,
                    var commandMetadata: CommandMetadata? = null,
                    var unsubscribeEndpoint: UnsubscribeEndpoint? = null
                ) {
                    @Keep
                    data class CommandMetadata(
                        var webCommandMetadata: WebCommandMetadata? = null
                    ) {
                        @Keep
                        data class WebCommandMetadata(
                            var apiUrl: String? = null,
                            var sendPost: Boolean? = null
                        )
                    }

                    @Keep
                    data class UnsubscribeEndpoint(
                        var channelIds: List<String?>? = null,
                        var params: String? = null
                    )
                }
            }
        }
    }

    @Keep
    data class ResponseContext(
        var mainAppWebResponseContext: MainAppWebResponseContext? = null,
        var serviceTrackingParams: List<ServiceTrackingParam?>? = null,
        var visitorData: String? = null,
        var webResponseContextExtensionData: WebResponseContextExtensionData? = null

    ) {
        @Keep
        data class MainAppWebResponseContext(
            var loggedOut: Boolean? = null
        )

        @Keep
        data class ServiceTrackingParam(
            var params: List<Param?>? = null,
            var service: String? = null
        ) {
            @Keep
            data class Param(
                var key: String? = null,
                var value: String? = null
            )
        }

        @Keep
        data class WebResponseContextExtensionData(
            var hasDecorated: Boolean? = null
        )
    }

    @Keep
    data class Storyboards(
        var playerLiveStoryboardSpecRenderer: PlayerLiveStoryboardSpecRenderer? = null
    ) {
        @Keep
        data class PlayerLiveStoryboardSpecRenderer(
            var spec: String? = null
        )
    }

    @Keep
    data class StreamingData(
        var adaptiveFormats: List<AdaptiveFormat?>? = null,
        var formats: List<Format?>? = null,
        var dashManifestUrl: String? = null,
        var expiresInSeconds: String? = null,
        var hlsManifestUrl: String? = null

    ) {

        @Keep
        data class Format(
            val approxDurationMs: String?,
            val audioChannels: Int?,
            val audioQuality: String?,
            val audioSampleRate: String?,
            val bitrate: Int?,
            val fps: Int?,
            val height: Int?,
            val itag: Int?,
            val lastModified: String?,
            val mimeType: String?,
            val projectionType: String?,
            val quality: String?,
            val qualityLabel: String?,
            val url: String?,
            val width: Int?

        )

        @Keep
        data class AdaptiveFormat(
            var audioChannels: Int? = null,
            var audioQuality: String? = null,
            var audioSampleRate: String? = null,
            var bitrate: Int? = null,
            var fps: Int? = null,
            var height: Int? = null,
            var highReplication: Boolean? = null,
            var itag: Int? = null,
            var lastModified: String? = null,
            var maxDvrDurationSec: Int? = null,
            var mimeType: String? = null,
            var projectionType: String? = null,
            var quality: String? = null,
            var qualityLabel: String? = null,
            var targetDurationSec: Int? = null,
            var url: String? = null,
            var width: Int? = null

        )
    }

    @Keep
    data class VideoDetails(
        var allowRatings: Boolean? = null,
        var author: String? = null,
        var averageRating: Double? = null,
        var channelId: String? = null,
        var isCrawlable: Boolean? = null,
        var isLive: Boolean? = null,
        var isLiveContent: Boolean? = null,
        var isLiveDvrEnabled: Boolean? = null,
        var isLowLatencyLiveStream: Boolean? = null,
        var isOwnerViewing: Boolean? = null,
        var isPrivate: Boolean? = null,
        var isUnpluggedCorpus: Boolean? = null,
        var keywords: List<String?>? = null,
        var latencyClass: String? = null,
        var lengthSeconds: String? = null,
        var liveChunkReadahead: Int? = null,
        var shortDescription: String? = null,
        var thumbnail: Thumbnail? = null,
        var title: String? = null,
        var videoId: String? = null,
        var viewCount: String? = null

    ) {
        @Keep
        data class Thumbnail(
            var thumbnails: List<Thumbnail?>? = null
        ) {
            @Keep
            data class Thumbnail(
                var height: Int? = null,
                var url: String? = null,
                var width: Int? = null
            )
        }
    }



}
