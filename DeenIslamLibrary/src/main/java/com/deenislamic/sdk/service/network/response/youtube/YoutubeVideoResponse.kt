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

    fun getUrl(qualityLabel: String): String? {
        return if (isLive()) {
            getHlsVideoUrl()
        } else {
            getVideoFromAdaptiveFormatsUrl(qualityLabel)
        }
    }

    /** qualityLabel: 480p,720p,1080p*/
    fun getVideoFromAdaptiveFormatsUrl(qualityLabel: String): String? {
        var videoUrl: String? = null
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return videoUrl
    }



    fun getHlsVideoUrl(): String? {
        return streamingData?.hlsManifestUrl
    }

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
            return obj != null

        } catch (e: Exception) {
        }
        return false
    }

    data class Attestation(
        var playerAttestationRenderer: PlayerAttestationRenderer? = null
    ) {

        data class PlayerAttestationRenderer(
            var botguardData: BotguardData? = null,
            var challenge: String? = null
        ) {
            data class BotguardData(
                var interpreterSafeUrl: InterpreterSafeUrl? = null,
                var program: String? = null
            ) {
                data class InterpreterSafeUrl(
                    var privateDoNotAccessOrElseTrustedResourceUrlWrappedValue: String? = null
                )
            }
        }
    }


    data class FrameworkUpdates(
        var entityBatchUpdate: EntityBatchUpdate? = null
    ) {
        data class EntityBatchUpdate(
            var mutations: List<Mutation?>? = null,
            var timestamp: Timestamp? = null
        ) {
            data class Mutation(
                var entityKey: String? = null,
                var payload: Payload? = null,
                var type: String? = null
            ) {
                data class Payload(
                    var offlineabilityEntity: OfflineabilityEntity? = null
                ) {
                    data class OfflineabilityEntity(
                        var accessState: String? = null,
                        var addToOfflineButtonState: String? = null,
                        var key: String? = null
                    )
                }
            }

            data class Timestamp(
                var nanos: Int? = null,
                var seconds: String? = null
            )
        }
    }

    data class HeartbeatParams(
        var heartbeatServerData: String? = null,
        var intervalMilliseconds: String? = null,
        var softFailOnError: Boolean? = null
    )

    data class Microformat(
        var playerMicroformatRenderer: PlayerMicroformatRenderer? = null
    ) {
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
            data class Description(
                var simpleText: String? = null
            )

            data class Embed(
                var flashSecureUrl: String? = null,
                var flashUrl: String? = null,
                var height: Int? = null,
                var iframeUrl: String? = null,
                var width: Int? = null
            )

            data class LiveBroadcastDetails(
                var isLiveNow: Boolean? = null,
                var startTimestamp: String? = null
            )

            data class Thumbnail(
                var thumbnails: List<Thumbnail?>? = null
            ) {
                data class Thumbnail(
                    var height: Int? = null,
                    var url: String? = null,
                    var width: Int? = null
                )
            }

            data class Title(
                var simpleText: String? = null
            )
        }
    }

    data class PlayabilityStatus(
        var contextParams: String? = null,
        var liveStreamability: LiveStreamability? = null,
        var miniplayer: Miniplayer? = null,
        var playableInEmbed: Boolean? = null,
        var status: String? = null
    ) {
        data class LiveStreamability(
            var liveStreamabilityRenderer: LiveStreamabilityRenderer? = null
        ) {
            data class LiveStreamabilityRenderer(
                var broadcastId: String? = null,
                var pollDelayMs: String? = null,
                var videoId: String? = null
            )
        }

        data class Miniplayer(
            var miniplayerRenderer: MiniplayerRenderer? = null
        ) {
            data class MiniplayerRenderer(
                var playbackMode: String? = null
            )
        }
    }

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
        data class AtrUrl(
            var baseUrl: String? = null,
            var elapsedMediaTimeSeconds: Int? = null
        )

        data class PtrackingUrl(
            var baseUrl: String? = null
        )

        data class QoeUrl(
            var baseUrl: String? = null
        )

        data class VideostatsDelayplayUrl(
            var baseUrl: String? = null,
            var elapsedMediaTimeSeconds: Int? = null
        )

        data class VideostatsPlaybackUrl(
            var baseUrl: String? = null
        )

        data class VideostatsWatchtimeUrl(
            var baseUrl: String? = null
        )
    }

    data class PlayerConfig(
        var audioConfig: AudioConfig? = null,
        var streamSelectionConfig: StreamSelectionConfig? = null,
        var vrConfig: VrConfig? = null,
        var webPlayerConfig: WebPlayerConfig? = null
    ) {
        data class AudioConfig(
            var loudnessDb: Double? = null,
            var perceptualLoudnessDb: Double? = null
        )

        data class StreamSelectionConfig(
            var maxBitrate: String? = null,
            var minBitrate: String? = null
        )

        data class VrConfig(
            var vr: Vr? = null
        ) {
            data class Vr(
                var enableVr: Boolean? = null
            )
        }

        data class WebPlayerConfig(
            var webPlayerActionsPorting: List<WebPlayerActionsPorting?>? = null,
            var webPlayerActionsTrackingParams: String? = null
        ) {
            data class WebPlayerActionsPorting(
                var clickTrackingParams: String? = null,
                var commandMetadata: CommandMetadata? = null,
                var createCommentDialogCommand: CreateCommentDialogCommand? = null,
                var deleteCommentCommand: DeleteCommentCommand? = null,
                var markAsHeartedCommand: MarkAsHeartedCommand? = null,
                var sendAddToPlaylistCommand: SendAddToPlaylistCommand? = null
            ) {
                data class CommandMetadata(
                    var webCommandMetadata: WebCommandMetadata? = null
                ) {
                    data class WebCommandMetadata(
                        var sendPost: Boolean? = null
                    )
                }

                data class CreateCommentDialogCommand(
                    var triggerDialog: Boolean? = null
                )

                data class DeleteCommentCommand(
                    var clickTrackingParams: String? = null
                )

                data class MarkAsHeartedCommand(
                    var clickTrackingParams: String? = null
                )

                data class SendAddToPlaylistCommand(
                    var clickTrackingParams: String? = null,
                    var commandMetadata: CommandMetadata? = null,
                    var webPlayerActionsPorting: List<WebPlayerActionsPorting?>? = null
                )
            }
        }
    }

    data class ResponseContext(
        var serviceTrackingParams: List<ServiceTrackingParam?>? = null
    ) {
        data class ServiceTrackingParam(
            var params: List<Param?>? = null,
            var service: String? = null
        ) {
            data class Param(
                var key: String? = null,
                var value: String? = null
            )
        }
    }

    data class Storyboards(
        var playerStoryboardSpecRenderer: PlayerStoryboardSpecRenderer? = null
    ) {
        data class PlayerStoryboardSpecRenderer(
            var spec: Spec? = null
        ) {
            data class Spec(
                var columns: Int? = null,
                var rows: Int? = null
            )
        }
    }

    data class StreamingData(
        var adaptiveFormats: List<AdaptiveFormat?>? = null,
        var expiresInSeconds: String? = null,
        var formats: List<Format?>? = null,
        var hlsManifestUrl: String? = null,
        var isLive: Boolean? = null,
        var segUrl: String? = null,
        var url: String? = null,
        var vssHost: String? = null
    ) {
        data class AdaptiveFormat(
            var approxDurationMs: String? = null,
            var bitrate: String? = null,
            var container: String? = null,
            var fps: Int? = null,
            var indexRange: IndexRange? = null,
            var initRange: InitRange? = null,
            var mimeType: String? = null,
            var projectionType: String? = null,
            var quality: String? = null,
            var qualityLabel: String? = null,
            var url: String? = null,
            var width: Int? = null
        ) {
            data class IndexRange(
                var end: String? = null,
                var start: String? = null
            )

            data class InitRange(
                var end: String? = null,
                var start: String? = null
            )
        }

        data class Format(
            var audioChannels: Int? = null,
            var audioQuality: String? = null,
            var audioSampleRate: String? = null,
            var bitrate: String? = null,
            var codecs: String? = null,
            var container: String? = null,
            var fps: Int? = null,
            var height: Int? = null,
            var indexRange: IndexRange? = null,
            var initRange: InitRange? = null,
            var mimeType: String? = null,
            var projectionType: String? = null,
            var quality: String? = null,
            var qualityLabel: String? = null,
            var url: String? = null,
            var width: Int? = null
        ) {
            data class IndexRange(
                var end: String? = null,
                var start: String? = null
            )

            data class InitRange(
                var end: String? = null,
                var start: String? = null
            )
        }
    }


    data class VideoDetails(
        var ageGating: AgeGating? = null,
        var allowRatings: Boolean? = null,
        var author: String? = null,
        var channelId: String? = null,
        var defaultAudioLanguage: String? = null,
        var isCrawlable: Boolean? = null,
        var isLiveContent: Boolean? = null,
        var isOwnerViewing: Boolean? = null,
        var isPrivate: Boolean? = null,
        var isUnpluggedCorpus: Boolean? = null,
        var isUpcoming: Boolean? = null,
        var keywords: String? = null,
        var lengthSeconds: String? = null,
        var shortDescription: String? = null,
        var title: String? = null
    ) {
        data class AgeGating(
            var alcoholContent: Boolean? = null,
            var restricted: Boolean? = null
        )
    }
}
