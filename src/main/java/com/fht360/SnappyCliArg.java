package com.fht360;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class SnappyCliArg {
    @Parameter(names = "-help", help = true)
    protected boolean help;

    @Parameter(names = "-dir", description = "specify a directory")
    public String directory;

    @Parameter(names = "-file", description = "specify a file")
    public String file;

    @Parameter(names = "-keep", description = "keep original snappy file, default false")
    public boolean keepOriginal = false;

    private void validate() {
        if (directory != null && file != null) {
            throw new IllegalStateException("please specify only one of -file or -dir, not both");
        } else if (directory == null && file == null) {
            throw new IllegalStateException("please specify exactly one of -file or -dir");
        }
    }

    public static SnappyCliArg fromArgs(String[] a) {
        SnappyCliArg args = new SnappyCliArg();
        JCommander jct = JCommander.newBuilder().addObject(args).build();
        jct.parse(a);
        if (args.help) {
            jct.usage();
            System.exit(0);
        }
        args.validate();
        return args;
    }


}
