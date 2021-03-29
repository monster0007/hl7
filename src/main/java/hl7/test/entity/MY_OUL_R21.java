package hl7.test.entity;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractMessage;
import ca.uhn.hl7v2.model.v24.segment.MSH;
import ca.uhn.hl7v2.model.v24.segment.OBR;
import ca.uhn.hl7v2.model.v24.segment.OBX;
import ca.uhn.hl7v2.model.v24.segment.PID;
import ca.uhn.hl7v2.parser.DefaultModelClassFactory;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import ca.uhn.hl7v2.parser.PipeParser;
import hl7.test.util.Hl7StringConverter;

import java.util.*;

public class MY_OUL_R21 extends AbstractMessage {

    public MY_OUL_R21() {
        this(new DefaultModelClassFactory());
    }

    public MY_OUL_R21(ModelClassFactory factory) {
        super(factory);
        init(factory);
        setParser(new PipeParser());
    }

    private void init(ModelClassFactory factory) {
        try {
            this.add(MSH.class, true, false);
            this.add(PID.class, true, true);
            this.add(OBR.class, false, true);
            this.add(OBX.class, false, true);
        } catch (HL7Exception e) {
            log.error("Unexpected error creating ORU_R01 - this is probably a bug in the source code generator.", e);
        }
    }

    public String getVersion() {
        return "2.4";
    }

    public MSH getMSH() {
        return getTyped("MSH", MSH.class);
    }

    public PID getPID() {
        return getTyped("PID", PID.class);
    }

    public OBR getOBR() {
        return getTyped("OBR", OBR.class);
    }

    public OBX getOBX() {
        return getTyped("OBX", OBX.class);
    }

    /**
     * 根据名称获取对应消息段
     * @param name
     * @return
     */
    public MSH getMSH(String name) {
        return getTyped(name, MSH.class);
    }

    public PID getPID(String name) {
        return getTyped(name, PID.class);
    }

    public OBR getOBR(String name) {
        return getTyped(name, OBR.class);
    }

    public OBX getOBX(String name) {
        return getTyped(name, OBX.class);
    }

    public List<OBX> getOBXList(String name) {
        List<OBX> OBXS = new ArrayList<OBX>();
        try {
             OBXS.addAll(getAllAsList(name, OBX.class));
        } catch (HL7Exception e) {
            e.printStackTrace();
        }
        return OBXS;
    }

    public List<OBX> getAllData() throws HL7Exception {
        String[] names = getNames();
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<OBX> OBXS = new ArrayList<OBX>();
        OBXS.addAll(getAllAsList("OBX", OBX.class));
        for (int i = 0; i < Hl7StringConverter.getConNum("pid", names); i++) {
            Map<String, Object> pidMap = new HashMap<String, Object>();
//            String pidName =

            for (String name : names) {

            }

        }
        return OBXS;
    }

    public String[] getNamesArr() {
        return getNames();
    }

    public List<OBX> getAllOBX() {
        String[] names = getNames();
        System.out.println(Arrays.toString(names).toString());
        List<OBX> OBXS = new ArrayList<OBX>();
        try {
            for (String name : names) {
                if (name.contains("OBX")) {
                    OBXS.addAll(getAllAsList(name, OBX.class));
                }
            }
            return OBXS;
        } catch (HL7Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
