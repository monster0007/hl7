package hl7.test;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.HL7Service;
import ca.uhn.hl7v2.app.Initiator;
import ca.uhn.hl7v2.app.ServerConfiguration;
import ca.uhn.hl7v2.hoh.llp.Hl7OverHttpLowerLayerProtocol;
import ca.uhn.hl7v2.hoh.util.ServerRoleEnum;
import ca.uhn.hl7v2.llp.LowerLayerProtocol;
import ca.uhn.hl7v2.llp.MinLowerLayerProtocol;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.PipeParser;
import hl7.test.listener.MyConnectionListener;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Hl7ClientTest {
//    private static int PORT_NUMBER = 8082;
    private static HapiContext context = new DefaultHapiContext();

    public static void main(String[] args) throws Exception {
//        if (args.length > 0) {
//            PORT_NUMBER = Integer.parseInt(args[0]);
//        }
//        System.out.println(PORT_NUMBER);
        try {
//            ThreadPoolExecutor executor = new ThreadPoolExecutor(
//                    10, 100,
//                    30, TimeUnit.SECONDS,
//                    new ArrayBlockingQueue<Runnable>(100));
//            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//
//            MinLowerLayerProtocol mllp = new MinLowerLayerProtocol();
//            mllp.setCharset("UTF-8");
//            context.setLowerLayerProtocol(mllp);
//            context.setExecutorService(executor);
//            ServerConfiguration s = new ServerConfiguration();
//            boolean useSecureConnection = false; // are you using TLS/SSL?
//            HL7Service ourHl7Server = context.newServer(8083, useSecureConnection);
//            ourHl7Server.registerConnectionListener(new MyConnectionListener());
//            ourHl7Server.registerApplication(new MyReceivingApplication());
//            ourHl7Server.startAndWait();
//            LowerLayerProtocol llp = new Hl7OverHttpLowerLayerProtocol(ServerRoleEnum.CLIENT);
//            context.setLowerLayerProtocol(llp);
            Connection connection = context.newClient("localhost",8082,false);
//            connection.activate();


//            int orderNum = 1;
//
//
            PipeParser pipeParser = context.getPipeParser();
            Initiator initiator = connection.getInitiator();
            String msg = "MSH|^~\\&|LIS||HTCIS||20201207101411||OUL^R21|U6680658|P|2.4|||AL|AL\r"
                    + "PID||0001042069|0001042069||谢继韬|||M\r"
                    + "PV1||O|^^^0305^肾内科门诊||||||001287||||||||||5407031|||||||||||||||||||||||||20201207093847\r"
                    + "OBR|1|16032220|4237929723|F00000092055^电解质七项^Ordinary||20201207084203|||||||||2&血清|||肾内科门诊|0305|4861273||20201207101340||||||||||0179&肖晓蔚|0178&郭伟权\r"
                    + "NTE|1\r"
                    + "OBX|1|NM|12081^钾(K)||3.98|mmol/L|3.50-5.30||||F|||20201207093847\r"
                    + "OBX|2|NM|12071^钠(Na)||143.00|mmol/L|137-147||||F|||20201207093847\r"
                    + "OBX|3|NM|12063^氯(Cl)||104|mmol/L|99-110||||F|||20201207093847\r"
                    + "OBX|4|NM|12062^钙(Ca)||2.50|mmol/L|2.11-2.52||||F|||20201207093847\r"
                    + "OBX|5|NM|12057^磷(P)||0.87|mmol/L|0.85-1.51||||F|||20201207093847\r"
                    + "OBX|6|NM|12054^镁(Mg)||0.76|mmol/L|0.75-1.02||||F|||20201207093847\r"
                    + "OBX|7|NM|12053^总二氧化碳(CO2)||24.00|mmoll/L|21-31||||F|||20201207093847\r";
            Message parse = pipeParser.parse(msg);
            while (true){
                Thread.sleep(3000);
                Message message = initiator.sendAndReceive(parse);
                System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaa");
                System.out.println(message.encode());
                System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaa");
            }

//            initiator.setTimeout(60000,TimeUnit.MINUTES);

//            Message message = null;
////            while(true){
//                String time = String.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS", new Date());
//                String msg = "MSH|^~\\&|LIS||||" + time + "||ACK^R01|" + orderNum + "|P|2.3.1||||||UNICODE\rMSA|AA|" + orderNum;
//                System.out.println(msg);
//                Message parse = pipeParser.parse(msg);
//                message = initiator.sendAndReceive(parse);
//                orderNum++;
//                System.out.println(message.encode());
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
