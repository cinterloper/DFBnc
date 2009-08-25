/*
 * Copyright (c) 2006-2007 Shane Mc Cormack
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
 *
 */

package uk.org.dataforce.dfbnc.commands.user;

import uk.org.dataforce.dfbnc.Account;
import uk.org.dataforce.dfbnc.AccountManager;
import uk.org.dataforce.dfbnc.commands.Command;
import uk.org.dataforce.dfbnc.commands.CommandManager;
import uk.org.dataforce.dfbnc.sockets.UserSocket;

/**
 * This file represents the 'password' command
 */
public class PasswordCommand extends Command {

    /**
     * Handle a version command.
     *
     * @param user the UserSocket that performed this command
     * @param params Params for command (param 0 is the command name)
     */
    @Override
    public void handle(final UserSocket user, final String[] params) {
        final Account account;
        final String password;
        final String username;
        if (params.length == 2) {
            username = user.getAccount().getName();
            password = params[1];
            account = user.getAccount();
        } else if (params.length == 3) {
            username = params[1];
            password = params[2];
            account = getAccount(params[1]);
        } else {
            if (user.getAccount().isAdmin()) {
                user.sendBotMessage("passwd [user] newpasswd", "");
            } else {
                user.sendBotMessage("passwd newpasswd", "");
            }
            return;
        }
        if (account == null) {
            user.sendBotMessage("Account %s doesnt exist", username);
        } else {
            account.setPassword(password);
            user.sendBotMessage("Password successfully changed to: %s", password);
        }
    }

    private Account getAccount(final String username) {
        if (AccountManager.exists(username)) {
            return AccountManager.get(username);
        } else {
            return null;
        }
    }

    /**
     * What does this Command handle.
     *
     * @return String[] with the names of the tokens we handle.
     */
    @Override
    public String[] handles() {
        return new String[]{"password", "passwd", "setpassword"};
    }

    /**
     * Create a new instance of the Command Object
     *
     * @param manager CommandManager that is in charge of this Command
     */
    public PasswordCommand(final CommandManager manager) {
        super(manager);
    }

    /**
     * Get a description of what this command does
     *
     * @param command The command to describe (incase one Command does multiple
     *                things under different names)
     * @return A description of what this command does
     */
    @Override
    public String getDescription(final String command) {
        return "This command sets a users password";
    }

    /**
     * Get detailed help for this command.
     *
     * @param params Parameters the user wants help with.
     *               params[0] will be the command name.
     * @return String[] with the lines to send to the user as the help, or null
     *         if no detailed help is available.
     */
    @Override
    public String[] getHelp(final String[] params) {
        return new String[]{
            "passwd [user] newpasswd",
            "Sets the password for yourself or the specified user (only " +
                    "admins can set other peoples passwords"
        };
    }
}