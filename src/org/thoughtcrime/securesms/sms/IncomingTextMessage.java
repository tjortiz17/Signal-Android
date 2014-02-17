package org.thoughtcrime.securesms.sms;

import android.os.Parcel;
import android.os.Parcelable;
import android.telephony.SmsMessage;

import org.thoughtcrime.securesms.database.model.SmsMessageRecord;
import org.thoughtcrime.securesms.util.GroupUtil;
import org.whispersystems.textsecure.push.IncomingPushMessage;
import org.whispersystems.textsecure.storage.RecipientDevice;

import java.util.List;

import ws.com.google.android.mms.pdu.SendReq;

import static org.whispersystems.textsecure.push.PushMessageProtos.PushMessageContent.GroupContext;

public class IncomingTextMessage implements Parcelable {

  public static final Parcelable.Creator<IncomingTextMessage> CREATOR = new Parcelable.Creator<IncomingTextMessage>() {
    @Override
    public IncomingTextMessage createFromParcel(Parcel in) {
      return new IncomingTextMessage(in);
    }

    @Override
    public IncomingTextMessage[] newArray(int size) {
      return new IncomingTextMessage[size];
    }
  };

  private final String  message;
  private final String  sender;
  private final int     senderDeviceId;
  private final int     protocol;
  private final String  serviceCenterAddress;
  private final boolean replyPathPresent;
  private final String  pseudoSubject;
  private final long    sentTimestampMillis;
  private final String  groupId;
  private final int     groupAction;
  private final String  groupActionArgument;

  public IncomingTextMessage(SmsMessage message) {
    this.message              = message.getDisplayMessageBody();
    this.sender               = message.getDisplayOriginatingAddress();
    this.senderDeviceId       = RecipientDevice.DEFAULT_DEVICE_ID;
    this.protocol             = message.getProtocolIdentifier();
    this.serviceCenterAddress = message.getServiceCenterAddress();
    this.replyPathPresent     = message.isReplyPathPresent();
    this.pseudoSubject        = message.getPseudoSubject();
    this.sentTimestampMillis  = message.getTimestampMillis();
    this.groupId              = null;
    this.groupAction          = -1;
    this.groupActionArgument  = null;
  }

  public IncomingTextMessage(IncomingPushMessage message, String encodedBody, GroupContext group) {
    this.message              = encodedBody;
    this.sender               = message.getSource();
    this.senderDeviceId       = message.getSourceDevice();
    this.protocol             = 31337;
    this.serviceCenterAddress = "GCM";
    this.replyPathPresent     = true;
    this.pseudoSubject        = "";
    this.sentTimestampMillis  = message.getTimestampMillis();

    if (group != null) {
      this.groupId             = GroupUtil.getEncodedId(group.getId().toByteArray());
      this.groupAction         = group.getType().getNumber();
      this.groupActionArgument = GroupUtil.serializeArguments(group);
    } else {
      this.groupId             = null;
      this.groupAction         = -1;
      this.groupActionArgument = null;
    }
  }

  public IncomingTextMessage(Parcel in) {
    this.message              = in.readString();
    this.sender               = in.readString();
    this.senderDeviceId       = in.readInt();
    this.protocol             = in.readInt();
    this.serviceCenterAddress = in.readString();
    this.replyPathPresent     = (in.readInt() == 1);
    this.pseudoSubject        = in.readString();
    this.sentTimestampMillis  = in.readLong();
    this.groupId              = in.readString();
    this.groupAction          = in.readInt();
    this.groupActionArgument  = in.readString();
  }

  public IncomingTextMessage(IncomingTextMessage base, String newBody) {
    this.message              = newBody;
    this.sender               = base.getSender();
    this.senderDeviceId       = base.getSenderDeviceId();
    this.protocol             = base.getProtocol();
    this.serviceCenterAddress = base.getServiceCenterAddress();
    this.replyPathPresent     = base.isReplyPathPresent();
    this.pseudoSubject        = base.getPseudoSubject();
    this.sentTimestampMillis  = base.getSentTimestampMillis();
    this.groupId              = base.getGroupId();
    this.groupAction          = base.getGroupAction();
    this.groupActionArgument  = base.getGroupActionArgument();
  }

  public IncomingTextMessage(List<IncomingTextMessage> fragments) {
    StringBuilder body = new StringBuilder();

    for (IncomingTextMessage message : fragments) {
      body.append(message.getMessageBody());
    }

    this.message              = body.toString();
    this.sender               = fragments.get(0).getSender();
    this.senderDeviceId       = fragments.get(0).getSenderDeviceId();
    this.protocol             = fragments.get(0).getProtocol();
    this.serviceCenterAddress = fragments.get(0).getServiceCenterAddress();
    this.replyPathPresent     = fragments.get(0).isReplyPathPresent();
    this.pseudoSubject        = fragments.get(0).getPseudoSubject();
    this.sentTimestampMillis  = fragments.get(0).getSentTimestampMillis();
    this.groupId              = fragments.get(0).getGroupId();
    this.groupAction          = fragments.get(0).getGroupAction();
    this.groupActionArgument  = fragments.get(0).getGroupActionArgument();
  }

  public IncomingTextMessage(SendReq record) {
    this.message              = "";
    this.sender               = record.getTo()[0].getString();
    this.senderDeviceId       = RecipientDevice.DEFAULT_DEVICE_ID;
    this.protocol             = 31338;
    this.serviceCenterAddress = "Outgoing";
    this.replyPathPresent     = true;
    this.pseudoSubject        = "";
    this.sentTimestampMillis  = System.currentTimeMillis();
    this.groupId              = null;
    this.groupAction          = -1;
    this.groupActionArgument  = null;
  }

  public IncomingTextMessage(SmsMessageRecord record) {
    this.message              = record.getBody().getBody();
    this.sender               = record.getIndividualRecipient().getNumber();
    this.senderDeviceId       = RecipientDevice.DEFAULT_DEVICE_ID;
    this.protocol             = 31338;
    this.serviceCenterAddress = "Outgoing";
    this.replyPathPresent     = true;
    this.pseudoSubject        = "";
    this.sentTimestampMillis  = System.currentTimeMillis();
    this.groupId              = null;
    this.groupAction          = -1;
    this.groupActionArgument  = null;
  }

  protected IncomingTextMessage(String sender, String groupId,
                                int groupAction, String groupActionArgument)
  {
    this.message              = "";
    this.sender               = sender;
    this.senderDeviceId       = RecipientDevice.DEFAULT_DEVICE_ID;
    this.protocol             = 31338;
    this.serviceCenterAddress = "Outgoing";
    this.replyPathPresent     = true;
    this.pseudoSubject        = "";
    this.sentTimestampMillis  = System.currentTimeMillis();
    this.groupId              = groupId;
    this.groupAction          = groupAction;
    this.groupActionArgument  = groupActionArgument;
  }

  public long getSentTimestampMillis() {
    return sentTimestampMillis;
  }

  public String getPseudoSubject() {
    return pseudoSubject;
  }

  public String getMessageBody() {
    return message;
  }

  public IncomingTextMessage withMessageBody(String message) {
    return new IncomingTextMessage(this, message);
  }

  public String getSender() {
    return sender;
  }

  public int getSenderDeviceId() {
    return senderDeviceId;
  }

  public int getProtocol() {
    return protocol;
  }

  public String getServiceCenterAddress() {
    return serviceCenterAddress;
  }

  public boolean isReplyPathPresent() {
    return replyPathPresent;
  }

  public boolean isKeyExchange() {
    return false;
  }

  public boolean isSecureMessage() {
    return false;
  }

  public boolean isPreKeyBundle() {
    return false;
  }

  public boolean isIdentityUpdate() {
    return false;
  }

  public String getGroupId() {
    return groupId;
  }

  public int getGroupAction() {
    return groupAction;
  }

  public String getGroupActionArgument() {
    return groupActionArgument;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel out, int flags) {
    out.writeString(message);
    out.writeString(sender);
    out.writeInt(senderDeviceId);
    out.writeInt(protocol);
    out.writeString(serviceCenterAddress);
    out.writeInt(replyPathPresent ? 1 : 0);
    out.writeString(pseudoSubject);
    out.writeLong(sentTimestampMillis);
    out.writeString(groupId);
    out.writeInt(groupAction);
    out.writeString(groupActionArgument);
  }

  public static IncomingTextMessage createForLeavingGroup(String groupId, String user) {
    return new IncomingTextMessage(user, groupId, GroupContext.Type.QUIT_VALUE, null);
  }
}
