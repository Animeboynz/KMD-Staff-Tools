# -*- coding: utf-8 -*-
# Generated by the protocol buffer compiler.  DO NOT EDIT!
# NO CHECKED-IN PROTOBUF GENCODE
# source: protobuf_schema.proto
# Protobuf Python Version: 5.29.3
"""Generated protocol buffer code."""
from google.protobuf import descriptor as _descriptor
from google.protobuf import descriptor_pool as _descriptor_pool
from google.protobuf import runtime_version as _runtime_version
from google.protobuf import symbol_database as _symbol_database
from google.protobuf.internal import builder as _builder
_runtime_version.ValidateProtobufRuntimeVersion(
    _runtime_version.Domain.PUBLIC,
    5,
    29,
    3,
    '',
    'protobuf_schema.proto'
)
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()




DESCRIPTOR = _descriptor_pool.Default().AddSerializedFile(b'\n\x15protobuf_schema.proto\x12\x18\x63om.animeboynz.kmd.proto\"\x91\x01\n\rBackupBarcode\x12\x0b\n\x03sku\x18\x01 \x01(\t\x12\r\n\x05\x63olor\x18\x02 \x01(\t\x12\x0c\n\x04size\x18\x03 \x01(\t\x12\x0c\n\x04name\x18\x04 \x01(\t\x12\x1a\n\rpiece_barcode\x18\x05 \x01(\tH\x00\x88\x01\x01\x12\x11\n\x04gtin\x18\x06 \x01(\tH\x01\x88\x01\x01\x42\x10\n\x0e_piece_barcodeB\x07\n\x05_gtin\"5\n\x0b\x42\x61\x63kupColor\x12\x12\n\ncolor_code\x18\x01 \x01(\t\x12\x12\n\ncolor_name\x18\x02 \x01(\t\"~\n\nBackupData\x12\x39\n\x08\x62\x61rcodes\x18\x01 \x03(\x0b\x32\'.com.animeboynz.kmd.proto.BackupBarcode\x12\x35\n\x06\x63olors\x18\x02 \x03(\x0b\x32%.com.animeboynz.kmd.proto.BackupColorb\x06proto3')

_globals = globals()
_builder.BuildMessageAndEnumDescriptors(DESCRIPTOR, _globals)
_builder.BuildTopDescriptorsAndMessages(DESCRIPTOR, 'protobuf_schema_pb2', _globals)
if not _descriptor._USE_C_DESCRIPTORS:
  DESCRIPTOR._loaded_options = None
  _globals['_BACKUPBARCODE']._serialized_start=52
  _globals['_BACKUPBARCODE']._serialized_end=197
  _globals['_BACKUPCOLOR']._serialized_start=199
  _globals['_BACKUPCOLOR']._serialized_end=252
  _globals['_BACKUPDATA']._serialized_start=254
  _globals['_BACKUPDATA']._serialized_end=380
# @@protoc_insertion_point(module_scope)
