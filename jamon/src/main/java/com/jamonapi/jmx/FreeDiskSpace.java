package com.jamonapi.jmx;

import java.io.File;

/**
 * Give free disk space. On my mac getFreeSpace() roughly maps to 'Avail', and
 * getTotalSpace() maps to 'Size'.  I used the definition of 1000 as that seems to be the more standard
 * definition for drive sizes.  Note units for the methods in this calls are in GB.
 *
 * df -H
 *   Filesystem      Size   Used  Avail Capacity  iused    ifree %iused  Mounted on
 *  /dev/disk1      499G   210G   289G    43% 51291254 70507400   42%
 */
public class FreeDiskSpace {
    static final double GB = 1000L*1000L*1000L;
    private File dir;

    /**
     * Defaults to root directory.
     */
    public FreeDiskSpace() {
        this(new File(File.separator));
    }

    /**
     *
     * @param dir absolute directory location to check diskspace for
     */
    public FreeDiskSpace(File dir) {
        this.dir = dir;
    }

    /**
     *
     * @return free space in GB
     */
    public double getFreeSpace() {
        return dir.getFreeSpace()/GB;
    }

    /**
     *
     * @return freeSpace/totalSpace
     */
    public double getFreeSpacePercent() {
        return getFreeSpace()/getTotalSpace();
    }

    /**
     *
     * @return total space in GB
     */
    public double getTotalSpace() {
        return dir.getTotalSpace()/GB;
    }

    public String getAbsolutePath() {
        return dir.getAbsolutePath();
    }

    public String toString() {
        return "path="+getAbsolutePath()+", freeSpace="+getFreeSpace()+" GB, totalSpace="+getTotalSpace()+" GB, freeSpacePercent="+getFreeSpacePercent();
    }
    
}
