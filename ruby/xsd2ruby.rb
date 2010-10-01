require 'rxsd'
require 'active_support'
require 'active_support/core_ext/string'
require 'time'
require 'date'

  xsd_uri = "file:///Users/ezamudio/Projects/FacturaZen/src/cfdv2.xsd"
  xml_uri = "file:///Users/ezamudio/Projects/FacturaZen/src/ejemplo.xml"

  schema = RXSD::Parser.parse_xsd :uri => xsd_uri

  puts "=======Classes======="
  classes = schema.to :ruby_classes
  puts classes.collect{ |cl| !cl.nil? ? (cl.to_s + " < " + cl.superclass.to_s) : ""}.sort.join("\n")

  puts "=======Tags======="
  puts schema.tags.collect { |n,cb| n + ": " + cb.to_s + ": " + (cb.nil? ? "ncb" : cb.klass_name.to_s + "-" + cb.klass.to_s) }.sort.join("\n")

  puts "Vamos a generar una factura"
  fact = Comprobante.new :version => "2.0", :serie => "PRUEBA", :folio => "123456", :subTotal => 100, :total => 116
  conc = Concepto.new :descripcion => "Item de prueba", :cantidad => 2, :valorUnitario => 5, :importe => 10
  fact.conceptos = Conceptos.new
  fact.conceptos.add_concepto conc

  puts "=======Objects======="
  data = RXSD::Parser.parse_xml :uri => xml_uri
  objs = data.to :ruby_objects, :schema => schema
  objs.each {  |obj|
    puts "#{obj}"
  }
