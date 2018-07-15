import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.interledger.core.Condition;
import org.interledger.core.InterledgerAddress;
import org.interledger.core.InterledgerPreparePacket;
import org.interledger.quilt.jackson.InterledgerModule;
import org.interledger.quilt.jackson.conditions.Encoding;
import org.junit.Test;

import java.math.BigInteger;
import java.time.Instant;

public class ExperimentTest {
  @Test
  public void testIt() throws JsonProcessingException {
    InterledgerPreparePacket packet =
      InterledgerPreparePacket.builder()
        .amount(BigInteger.TEN)
        .destination(InterledgerAddress.of("g.bob.one.com"))
        .executionCondition(Condition.of(new byte[32]))
        .expiresAt(Instant.now().plusSeconds(60))
        .data(new byte[32])
        .build();

    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new InterledgerModule(Encoding.HEX));
    String json = mapper.writeValueAsString(packet);
    System.out.println(json);
  }
}
