/*
 * Copyright (c) 2006-2015 DFBnc Developers
 *
 * Where no other license is explicitly given or mentioned in the file, all files
 * in this project are licensed using the following license.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.dfbnc.sockets;

import com.dfbnc.ConnectionHandler;

/**
 * Debug Flags enabled for a given Socket.
 */
public enum DebugFlag {
    ServerDataIn("Server Data In", "IN", false),
    ServerDataOut("Server Data Out", "OUT", false),
    Logging("DFBNC Logging", "LOG", true);

    /** Name of this DebugFlag. */
    private final String name;

    /** Tag for this DebugFlag. */
    private final String tag;

    /** Is this DebugFlag only available for admins. */
    private final boolean adminOnly;

    /**
     * Create a new DebugFlag.
     *
     * @param name Name
     * @param tag Tag
     * @param adminOnly Admin Only Flag
     */
    DebugFlag(final String name, final String tag, final boolean adminOnly) {
        this.name = name;
        this.tag = tag;
        this.adminOnly = adminOnly;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Tag for this flag.
     *
     * @return Tag for this flag
     */
    public String getTag() {
        return tag;
    }

    /**
     * Is this flag admin only?
     *
     * @return true if only admins can enable this flag.
     */
    public boolean isAdminOnly() {
        return adminOnly;
    }
}
