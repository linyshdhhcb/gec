package com.gec.controller;

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesis;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesisParam;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesisResult;
import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesizer;
import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesisParam;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 阿里云百炼AI多媒体生成控制器
 * 提供文字生成图片、文字生成视频、图生视频、视频特效、文字生成音频等功能
 *
 * @author gec
 * @version 1.0
 */
@RestController
@RequestMapping("/ai")
public class TextToImageController {

    /**
     * 文字生成图片接口
     * 使用阿里云通义万相 qwen-image-plus 模型，根据文本描述生成高质量图片
     *
     * @param message 图片描述提示词，默认为"美女"
     * @return JSON格式的图片生成结果，包含图片URL等信息
     * @throws Exception 生成失败时抛出异常
     */
    @GetMapping("/texttoimage")
    public String texttoImage(@RequestParam(defaultValue = "美女") String message) throws Exception{

        ImageSynthesisParam param =
                ImageSynthesisParam.builder()
                        // 阿里云百炼API Key
                        .apiKey("sk-f8bbd652138e47648766bfe636324d1f")
                        // 模型名称：qwen-image-plus 或 qwen-image
                        .model("qwen-image-plus")
                        // 提示词
                        .prompt(message)
                        // 生成的张数（当前固定为1）
                        .n(1)
                        // 种子值，相同seed可使生成内容保持相对稳定
                        .seed(1234)
                        // 图片的大小，默认1328*1328
                        .size("1328*1328")
                        .build();

        ImageSynthesis imageSynthesis = new ImageSynthesis();

        ImageSynthesisResult result = imageSynthesis.call(param);

        return JsonUtils.toJson(result);
    }

    /**
     * 文字生成视频接口
     * 使用阿里云通义万相 wan2.5-t2v-preview 模型，根据文本描述和音频生成视频
     *
     * @param message 视频场景描述提示词，默认为小猫将军的史诗场景
     * @return JSON格式的视频生成结果，包含视频URL等信息
     * @throws Exception 生成失败时抛出异常
     */
    @RequestMapping("/texttovideo")
    public String texttovideo(@RequestParam(defaultValue = "一幅史诗级可爱的场景。一只小巧可爱的卡通小猫将军，身穿细节精致的金色盔甲，头戴一个稍大的头盔，勇敢地站在悬崖上。他骑着一匹虽小但英勇的战马，说：\"青海长云暗雪山，孤城遥望玉门关。黄沙百战穿金甲，不破楼兰终不还。\"。悬崖下方，一支由老鼠组成的、数量庞大、无穷无尽的军队正带着临时制作的武器向前冲锋。这是一个戏剧性的、大规模的战斗场景，灵感来自中国古代的战争史诗。远处的雪山上空，天空乌云密布。整体氛围是\"可爱\"与\"霸气\"的搞笑和史诗般的融合。") String message) throws Exception{
        VideoSynthesisParam param =
                VideoSynthesisParam.builder()
                        .apiKey("sk-f8bbd652138e47648766bfe636324d1f")
                        .model("wan2.5-t2v-preview")
                        .prompt(message)
                        .audioUrl("https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20250923/hbiayh/%E4%BB%8E%E5%86%9B%E8%A1%8C.mp3")
                        .size("832*480")
                        .duration(5)
                        // .audio(true)
                        .build();
        System.out.println("please wait...");

        VideoSynthesis vs = new VideoSynthesis();
        VideoSynthesisResult result = vs.call(param);
        return JsonUtils.toJson(result);
    }

    /**
     * 文字生成音频接口（语音合成TTS）
     * 使用阿里云CosyVoice cosyvoice-v2 模型，将文本转换为语音并保存为MP3文件
     *
     * @param message 待合成的文本内容，默认为《静夜思》
     * @return 操作结果提示信息
     */
    @RequestMapping("/texttoaudio")
    public String texttoaudio(@RequestParam(defaultValue = "静夜思 唐 李白 床前明月光,疑是地上霜,举头望明月,低头思故乡") String message){
        try {
            // 请求参数
            SpeechSynthesisParam param =
                    SpeechSynthesisParam.builder()
                            // 阿里云百炼API Key
                            .apiKey("sk-f8bbd652138e47648766bfe636324d1f")
                            .model("cosyvoice-v2") // 模型
                            .voice("longxiaochun_v2") // 音色
                            .build();

            // 创建语音合成器并传入参数
            SpeechSynthesizer synthesizer = new SpeechSynthesizer(param, null);

            // 调用语音合成
            ByteBuffer audio = synthesizer.call(message);

            // 任务结束关闭websocket连接
            synthesizer.getDuplexApi().close(1000, "bye");

            String filename = "output.mp3";

            if (audio != null) {
                // 将音频数据保存到本地文件"output.mp3"中
                File file = new File("src/main/resources/tts/" + filename);

                // 确保目录存在
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(audio.array());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return "音频生成完成..";
        } catch (Exception e) {
            e.printStackTrace();
            return "音频生成失败: " + e.getMessage();
        }
    }

    /**
     * 图生视频接口
     * 使用阿里云通义万相 wan2.5-i2v-preview 模型，根据静态图片和音频生成动态视频
     * 支持图片中的角色根据音频内容进行动作和口型同步
     *
     * @return JSON格式的视频生成结果，包含视频URL等信息
     * @throws Exception 生成失败时抛出异常
     */
    @RequestMapping("/imagetovide")
    public String imagetovide() throws Exception
    {

        VideoSynthesis vs = new VideoSynthesis();
        VideoSynthesisParam param =
                VideoSynthesisParam.builder()
                        .apiKey("sk-f8bbd652138e47648766bfe636324d1f")
                        .model("wan2.5-i2v-preview")
                        .prompt("一幅都市奇幻艺术的场景。一个充满动感的涂鸦艺术角色。一个由喷漆所画成的少年，正从一面混凝土墙上活过来。他一边用极快的语速演唱一首英文rap，一边摆着一个经典的、充满活力的说唱歌手姿势。场景设定在夜晚一个充满都市感的铁路桥下。灯光来自一盏孤零零的街灯，营造出电影般的氛围，充满高能量和惊人的细节。视频的音频部分完全由他的rap构成，没有其他对话或杂音。")
                        .imgUrl("https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20250925/wpimhv/rap.png")
                        .audioUrl("https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20250925/ozwpvi/rap.mp3")
                        .duration(5)
                        .size("832*480")
                        .build();
        System.out.println("please wait...");
        VideoSynthesisResult result = vs.call(param);
        System.out.println(JsonUtils.toJson(result));
        return JsonUtils.toJson(result);
    }

    /**
     * 视频特效生成接口
     * 使用阿里云通义万相 wanx2.1-kf2v-plus 模型，为静态图片添加特效动画
     * 支持多种模板效果（如汉服、古风等）
     *
     * @return JSON格式的视频生成结果，包含视频URL等信息
     * @throws Exception 生成失败时抛出异常
     */
    @RequestMapping("/videoeffect")
    public String videoeffect() throws Exception
    {
        System.out.println("开始生成视频...");
        
        VideoSynthesis vs = new VideoSynthesis();
        VideoSynthesisParam param =
                VideoSynthesisParam.builder()
                        .apiKey("sk-f8bbd652138e47648766bfe636324d1f")
                        .model("wanx2.1-kf2v-plus")
                        .firstFrameUrl("https://ty-yuanfang.oss-cn-hangzhou.aliyuncs.com/lizhengjia.lzj/tmp/11.png")
                        .duration(5)
                        .resolution("480P")
                        .template("hanfu-1")
                        .build();
        
        System.out.println("参数配置完成，调用API...");
        VideoSynthesisResult result = vs.call(param);
        
        System.out.println("视频生成完成");
        System.out.println(JsonUtils.toJson(result));
        return JsonUtils.toJson(result);
    }
}
