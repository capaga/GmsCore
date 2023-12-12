// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: POToken.proto

package com.google.android.mg.proto;

/**
 * Protobuf type {@code GetPoIntegrityTokenRequest}
 */
public final class GetPoIntegrityTokenRequest extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:GetPoIntegrityTokenRequest)
    GetPoIntegrityTokenRequestOrBuilder {
private static final long serialVersionUID = 0L;
  // Use GetPoIntegrityTokenRequest.newBuilder() to construct.
  private GetPoIntegrityTokenRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private GetPoIntegrityTokenRequest() {
    dgResult_ = com.google.protobuf.ByteString.EMPTY;
    dgRandKey_ = com.google.protobuf.ByteString.EMPTY;
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new GetPoIntegrityTokenRequest();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private GetPoIntegrityTokenRequest(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 8: {

            mode_ = input.readInt32();
            break;
          }
          case 18: {

            dgResult_ = input.readBytes();
            break;
          }
          case 26: {

            dgRandKey_ = input.readBytes();
            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.google.android.mg.proto.POToken.internal_static_GetPoIntegrityTokenRequest_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.google.android.mg.proto.POToken.internal_static_GetPoIntegrityTokenRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.google.android.mg.proto.GetPoIntegrityTokenRequest.class, com.google.android.mg.proto.GetPoIntegrityTokenRequest.Builder.class);
  }

  public static final int MODE_FIELD_NUMBER = 1;
  private int mode_;
  /**
   * <code>int32 mode = 1;</code>
   * @return The mode.
   */
  @java.lang.Override
  public int getMode() {
    return mode_;
  }

  public static final int DGRESULT_FIELD_NUMBER = 2;
  private com.google.protobuf.ByteString dgResult_;
  /**
   * <code>bytes dgResult = 2;</code>
   * @return The dgResult.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString getDgResult() {
    return dgResult_;
  }

  public static final int DGRANDKEY_FIELD_NUMBER = 3;
  private com.google.protobuf.ByteString dgRandKey_;
  /**
   * <code>bytes dgRandKey = 3;</code>
   * @return The dgRandKey.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString getDgRandKey() {
    return dgRandKey_;
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (mode_ != 0) {
      output.writeInt32(1, mode_);
    }
    if (!dgResult_.isEmpty()) {
      output.writeBytes(2, dgResult_);
    }
    if (!dgRandKey_.isEmpty()) {
      output.writeBytes(3, dgRandKey_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (mode_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(1, mode_);
    }
    if (!dgResult_.isEmpty()) {
      size += com.google.protobuf.CodedOutputStream
        .computeBytesSize(2, dgResult_);
    }
    if (!dgRandKey_.isEmpty()) {
      size += com.google.protobuf.CodedOutputStream
        .computeBytesSize(3, dgRandKey_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof com.google.android.mg.proto.GetPoIntegrityTokenRequest)) {
      return super.equals(obj);
    }
    com.google.android.mg.proto.GetPoIntegrityTokenRequest other = (com.google.android.mg.proto.GetPoIntegrityTokenRequest) obj;

    if (getMode()
        != other.getMode()) return false;
    if (!getDgResult()
        .equals(other.getDgResult())) return false;
    if (!getDgRandKey()
        .equals(other.getDgRandKey())) return false;
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + MODE_FIELD_NUMBER;
    hash = (53 * hash) + getMode();
    hash = (37 * hash) + DGRESULT_FIELD_NUMBER;
    hash = (53 * hash) + getDgResult().hashCode();
    hash = (37 * hash) + DGRANDKEY_FIELD_NUMBER;
    hash = (53 * hash) + getDgRandKey().hashCode();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.google.android.mg.proto.GetPoIntegrityTokenRequest parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.google.android.mg.proto.GetPoIntegrityTokenRequest parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.google.android.mg.proto.GetPoIntegrityTokenRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.google.android.mg.proto.GetPoIntegrityTokenRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.google.android.mg.proto.GetPoIntegrityTokenRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.google.android.mg.proto.GetPoIntegrityTokenRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.google.android.mg.proto.GetPoIntegrityTokenRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.google.android.mg.proto.GetPoIntegrityTokenRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.google.android.mg.proto.GetPoIntegrityTokenRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.google.android.mg.proto.GetPoIntegrityTokenRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.google.android.mg.proto.GetPoIntegrityTokenRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.google.android.mg.proto.GetPoIntegrityTokenRequest parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(com.google.android.mg.proto.GetPoIntegrityTokenRequest prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code GetPoIntegrityTokenRequest}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:GetPoIntegrityTokenRequest)
      com.google.android.mg.proto.GetPoIntegrityTokenRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.google.android.mg.proto.POToken.internal_static_GetPoIntegrityTokenRequest_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.google.android.mg.proto.POToken.internal_static_GetPoIntegrityTokenRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.google.android.mg.proto.GetPoIntegrityTokenRequest.class, com.google.android.mg.proto.GetPoIntegrityTokenRequest.Builder.class);
    }

    // Construct using com.google.android.mg.proto.GetPoIntegrityTokenRequest.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      mode_ = 0;

      dgResult_ = com.google.protobuf.ByteString.EMPTY;

      dgRandKey_ = com.google.protobuf.ByteString.EMPTY;

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.google.android.mg.proto.POToken.internal_static_GetPoIntegrityTokenRequest_descriptor;
    }

    @java.lang.Override
    public com.google.android.mg.proto.GetPoIntegrityTokenRequest getDefaultInstanceForType() {
      return com.google.android.mg.proto.GetPoIntegrityTokenRequest.getDefaultInstance();
    }

    @java.lang.Override
    public com.google.android.mg.proto.GetPoIntegrityTokenRequest build() {
      com.google.android.mg.proto.GetPoIntegrityTokenRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.google.android.mg.proto.GetPoIntegrityTokenRequest buildPartial() {
      com.google.android.mg.proto.GetPoIntegrityTokenRequest result = new com.google.android.mg.proto.GetPoIntegrityTokenRequest(this);
      result.mode_ = mode_;
      result.dgResult_ = dgResult_;
      result.dgRandKey_ = dgRandKey_;
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.google.android.mg.proto.GetPoIntegrityTokenRequest) {
        return mergeFrom((com.google.android.mg.proto.GetPoIntegrityTokenRequest)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.google.android.mg.proto.GetPoIntegrityTokenRequest other) {
      if (other == com.google.android.mg.proto.GetPoIntegrityTokenRequest.getDefaultInstance()) return this;
      if (other.getMode() != 0) {
        setMode(other.getMode());
      }
      if (other.getDgResult() != com.google.protobuf.ByteString.EMPTY) {
        setDgResult(other.getDgResult());
      }
      if (other.getDgRandKey() != com.google.protobuf.ByteString.EMPTY) {
        setDgRandKey(other.getDgRandKey());
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      com.google.android.mg.proto.GetPoIntegrityTokenRequest parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.google.android.mg.proto.GetPoIntegrityTokenRequest) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private int mode_ ;
    /**
     * <code>int32 mode = 1;</code>
     * @return The mode.
     */
    @java.lang.Override
    public int getMode() {
      return mode_;
    }
    /**
     * <code>int32 mode = 1;</code>
     * @param value The mode to set.
     * @return This builder for chaining.
     */
    public Builder setMode(int value) {
      
      mode_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 mode = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearMode() {
      
      mode_ = 0;
      onChanged();
      return this;
    }

    private com.google.protobuf.ByteString dgResult_ = com.google.protobuf.ByteString.EMPTY;
    /**
     * <code>bytes dgResult = 2;</code>
     * @return The dgResult.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString getDgResult() {
      return dgResult_;
    }
    /**
     * <code>bytes dgResult = 2;</code>
     * @param value The dgResult to set.
     * @return This builder for chaining.
     */
    public Builder setDgResult(com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      dgResult_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>bytes dgResult = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearDgResult() {
      
      dgResult_ = getDefaultInstance().getDgResult();
      onChanged();
      return this;
    }

    private com.google.protobuf.ByteString dgRandKey_ = com.google.protobuf.ByteString.EMPTY;
    /**
     * <code>bytes dgRandKey = 3;</code>
     * @return The dgRandKey.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString getDgRandKey() {
      return dgRandKey_;
    }
    /**
     * <code>bytes dgRandKey = 3;</code>
     * @param value The dgRandKey to set.
     * @return This builder for chaining.
     */
    public Builder setDgRandKey(com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      dgRandKey_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>bytes dgRandKey = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearDgRandKey() {
      
      dgRandKey_ = getDefaultInstance().getDgRandKey();
      onChanged();
      return this;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:GetPoIntegrityTokenRequest)
  }

  // @@protoc_insertion_point(class_scope:GetPoIntegrityTokenRequest)
  private static final com.google.android.mg.proto.GetPoIntegrityTokenRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.google.android.mg.proto.GetPoIntegrityTokenRequest();
  }

  public static com.google.android.mg.proto.GetPoIntegrityTokenRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<GetPoIntegrityTokenRequest>
      PARSER = new com.google.protobuf.AbstractParser<GetPoIntegrityTokenRequest>() {
    @java.lang.Override
    public GetPoIntegrityTokenRequest parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new GetPoIntegrityTokenRequest(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<GetPoIntegrityTokenRequest> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<GetPoIntegrityTokenRequest> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.android.mg.proto.GetPoIntegrityTokenRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

