package com.aidanogrady.qgrady;

/**
 * Created by Aidan O'Grady on 14/03/16.
 */
public class PrismMacros {
    public static final String EMPTY_LINE = "";

    public static final String MODEL_TYPE = "dtmc";

    public static final String MODULE = "module ";

    public static final String END_MODULE = "endmodule";

    private static final String SYNC = "SYNC";

    private static final String GUARD = "GUARD";

    private static final String ACTION = "ACTION";

    private static final String VAR = "VAR";

    private static final String VAL = "VAL";

    public static String inputModule(String var) {
        return MODULE + "INPUT_" + var;
    }

    public static String varDec(String variable, int range) {
        String varDec = "\tVAR : [-1..VAL] init - 1;";
        return varDec.replaceAll(VAL, Integer.toString(range)
                .replaceAll(VAR, variable));
    }

    public static String command(String sync, String guard, String action) {
        String command = "\t[SYNC] GUARD -> ACTION;";
        return command.replaceAll(SYNC, sync)
                .replaceAll(GUARD, guard)
                .replaceAll(ACTION, action);
    }

    public static String isEqual(String variable, int value) {
        String isEqual = "(VAR = VAL)";
        return isEqual.replaceAll(VAR, variable)
                .replaceAll(VAL, Integer.toString(value));
    }

    public static String isNotEqual(String variable, int value) {
        String isNotEqual = "(VAR = VAL)";
        return isNotEqual.replaceAll(VAR, variable)
                .replaceAll(VAL, Integer.toString(value));
    }

    public static String prob(double prob, String action) {
        String update = "VAL : ACTION";
        return update.replaceAll(VAL, Double.toString(prob))
                .replaceAll(ACTION, action);
    }

    public static String assign(String variable, int value) {
        String assign = "(VAR' = VAL)";
        return assign.replaceAll(VAL, Integer.toString(value))
                .replaceAll(VAR, variable);
    }

    public static String coinToss(String variable) {
        String coinToss = "0.5 : (VAR' =  0) + 0.5 : (VAR' = 1)";
        return coinToss.replaceAll(VAR, variable);
    }
}
