package com.asksunny.ffmt.compression;

import static parquet.Preconditions.checkNotNull;
import static parquet.schema.MessageTypeParser.parseMessageType;

import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;

import parquet.Preconditions;
import parquet.example.data.Group;
import parquet.example.data.GroupWriter;
import parquet.hadoop.api.WriteSupport;
import parquet.io.api.RecordConsumer;
import parquet.schema.MessageType;
import parquet.schema.MessageTypeParser;

public class GroupWriteSupportI extends WriteSupport<Group> {

  public static final String PARQUET_EXAMPLE_SCHEMA = "parquet.example.schema";

  public static void setSchema(MessageType schema, Configuration configuration) {
	
	  configuration.set(PARQUET_EXAMPLE_SCHEMA, schema.toString());
  }

  public static MessageType getSchema(Configuration configuration) {
	  System.out.println(configuration.toString());
	  String schema = configuration.get(PARQUET_EXAMPLE_SCHEMA);
	  System.out.println(schema);
	  return parseMessageType(schema);
  }

  private MessageType schema;
  private GroupWriter groupWriter;

  @Override
  public parquet.hadoop.api.WriteSupport.WriteContext init(Configuration configuration) {
	  
	  schema = getSchema(configuration);
    return new WriteContext(schema, new HashMap<String, String>());
  }

  @Override
  public void prepareForWrite(RecordConsumer recordConsumer) {
    groupWriter = new GroupWriter(recordConsumer, schema);
  }

  @Override
  public void write(Group record) {
    groupWriter.write(record);
  }

}