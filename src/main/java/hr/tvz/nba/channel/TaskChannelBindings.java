package hr.tvz.nba.channel;

import hr.tvz.nba.dto.OutputMessage;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;

public interface TaskChannelBindings {
    String INPUT =  "output_topic";

    @Input(INPUT)
    KStream<String, OutputMessage> input();
}
