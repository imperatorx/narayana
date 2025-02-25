/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.arjuna.mw.wsas.logging;

import org.jboss.logging.annotations.*;
import static org.jboss.logging.Logger.Level.*;
import static org.jboss.logging.annotations.Message.Format.*;

/**
 * i18n log messages for the wsas module.
 *
 * @author Jonathan Halliday (jonathan.halliday@redhat.com) 2010-06
 */
@MessageLogger(projectCode = "ARJUNA")
public interface wsasI18NLogger {

    /*
        Message IDs are unique and non-recyclable.
        Don't change the purpose of existing messages.
          (tweak the message text or params for clarification if you like).
        Allocate new messages by following instructions at the bottom of the file.
     */

    @Message(id = 41001, value = "allHighLevelServices threw exception", format = MESSAGE_FORMAT)
    @LogMessage(level = WARN)
	public void warn_context_ContextManager_1(@Cause() Throwable arg0);

	@Message(id = 41002, value = "assembling contexts and received exception", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_context_ContextManager_2(@Cause() Throwable arg0);

//	@Message(id = 41003, value = "not found.", format = MESSAGE_FORMAT)
//	@LogMessage(level = WARN)
//	public void warn_utils_Configuration_1();

	@Message(id = 41004, value = "Failed to create doc", format = MESSAGE_FORMAT)
	public String get_utils_Configuration_2();

	@Message(id = 41005, value = "Activity.start caught exception", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_UserActivityImple_1(@Cause() Throwable arg0);

	@Message(id = 41006, value = "currentActivity.end threw:", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_UserActivityImple_2(@Cause() Throwable arg0);

	@Message(id = 41007, value = "Activity.completed caught exception", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_UserActivityImple_3(@Cause() Throwable arg0);

	@Message(id = 41008, value = "Activity.suspended caught:", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_UserActivityImple_4(@Cause() Throwable arg0);

	@Message(id = 41009, value = "Activity.resumed caught exception", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_UserActivityImple_5(@Cause() Throwable arg0);

	@Message(id = 41010, value = "Unknown activity implementation!", format = MESSAGE_FORMAT)
	public String get_UserActivityImple_51();

	@Message(id = 41011, value = "State incompatible to start activity:", format = MESSAGE_FORMAT)
	public String get_activity_ActivityImple_1();

	@Message(id = 41012, value = "Cannot remove child activity from parent as parent''s status is:", format = MESSAGE_FORMAT)
	public String get_activity_ActivityImple_10();

	@Message(id = 41013, value = "Activity cannot complete as it has active children:", format = MESSAGE_FORMAT)
	public String get_activity_ActivityImple_2();

	@Message(id = 41014, value = "Cannot complete activity in status:", format = MESSAGE_FORMAT)
	public String get_activity_ActivityImple_3();

	@Message(id = 41015, value = "Cannot set completion status on activity as the status is incompatible:", format = MESSAGE_FORMAT)
	public String get_activity_ActivityImple_4();

	@Message(id = 41016, value = "Cannot change completion status, value is incompatible:", format = MESSAGE_FORMAT)
	public String get_activity_ActivityImple_5();

	@Message(id = 41017, value = "Cannot enlist null child!", format = MESSAGE_FORMAT)
	public String get_activity_ActivityImple_6();

	@Message(id = 41018, value = "Cannot enlist child activity with parent as parent''s status is:", format = MESSAGE_FORMAT)
	public String get_activity_ActivityImple_7();

	@Message(id = 41019, value = "Cannot remove null child!", format = MESSAGE_FORMAT)
	public String get_activity_ActivityImple_8();

	@Message(id = 41020, value = "The following child activity is unknown to the parent:", format = MESSAGE_FORMAT)
	public String get_activity_ActivityImple_9();

	@Message(id = 41021, value = "ActivityReaper: could not terminate.", format = MESSAGE_FORMAT)
	@LogMessage(level = WARN)
	public void warn_activity_ActivityReaper_1();

	@Message(id = 41022, value = "HLS not found!", format = MESSAGE_FORMAT)
	public String get_activity_HLSManager_1();

    /*
        Allocate new messages directly above this notice.
          - id: use the next id number in numeric sequence. Don't reuse ids.
          The first two digits of the id(XXyyy) denote the module
            all message in this file should have the same prefix.
          - value: default (English) version of the log message.
          - level: according to severity semantics defined at http://docspace.corp.redhat.com/docs/DOC-30217
          Debug and trace don't get i18n. Everything else MUST be i18n.
          By convention methods with String return type have prefix get_,
            all others are log methods and have prefix <level>_
     */
}