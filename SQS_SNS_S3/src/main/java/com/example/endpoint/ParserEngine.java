package com.example.endpoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
 
import com.example.parserEngine.ValueSet;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Component
public class ParserEngine {
	@Autowired
	 ObjectMapper mapper;
	
	 private static final String CLASS_PKG_NAME  = "class com.fasterxml.jackson.databind.node.";
	 private static final String ARRAY_NODE      = "ArrayNode";
	 private static final String TEXT_NODE       = "TextNode";
	 private static final String INT_NODE        = "IntNode";
	 private static final String DOUBLE_NODE     = "DoubleNode";
	 private static final String LONG_NODE       = "LongNode";
	 private static final String BOOLEAN_NODE    = "BooleanNode";
	 private int txtNodeConter = -1;
	 private int intNodeCounter = -1;
	 private int dblNodeCounter = -1;
	 private int lngNodeCounter = -1;
	 private int booleanNodeCounter = -1;
	 
	 @SuppressWarnings("deprecation")
	private ParserEngine() {
			mapper.setVisibilityChecker(mapper.getVisibilityChecker().withFieldVisibility(Visibility.ANY)
					.withGetterVisibility(Visibility.NONE).withIsGetterVisibility(Visibility.NONE));
	 }

		enum DataTypes {
			INT("int"), STRING("string"), LONG("long"), DOUBLE("double"), BOOLEAN("boolean");

			private final String name;

			private DataTypes(String s) {
				this.name = s;
			}

			public String toString() {
				return this.name;
			}
		}
	 
	
	 private int[] counter = { 0 };
	 private Supplier<List<ValueSet>> getGenericObjectWrapper = ()->{return new ArrayList<>();};
	 private HashMap<String, Object>             map = new LinkedHashMap<>();
	 private List<ValueSet>        			 intLst = getGenericObjectWrapper.get();
	 private List<ValueSet>        			 dblLst = getGenericObjectWrapper.get();
	 private List<ValueSet>        			 lngLst = getGenericObjectWrapper.get();
	 private List<ValueSet>    			 booleanLst = getGenericObjectWrapper.get();
	 private List<ValueSet>        			 strLst = getGenericObjectWrapper.get();
	 private List<String> supportedTypes = List.of(ARRAY_NODE, TEXT_NODE, INT_NODE, DOUBLE_NODE, LONG_NODE,BOOLEAN_NODE);
	 private Function<JsonNode, Boolean> checkchildObjectsState = (var childNodeCanidate) -> {return !childNodeCanidate.isNull() || !childNodeCanidate.isEmpty();};
	 private Function<Stream<?>, List<?>> convertStreamToList = (var stream) -> {return stream.collect(Collectors.toList());};
     private List<?> streamToList(Stream<?> stream) {return convertStreamToList.apply(stream.distinct());};
     private String returnKeyAppendedWithLstFlag(String keys, String nodeType) {return Optional.ofNullable(keys).map(String::valueOf).orElse("LIST_" + nodeType);}

		private BiPredicate<ObjectMapper, String> isKeyArray = (mapper, content) -> {
			try {
				return mapper.readTree(content).isArray();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		};

	 private String readValueFromNode(JsonNode node) {return node.getClass().toString().replace(CLASS_PKG_NAME, "").trim();}

	 private void populateRecordSet(long counter, HashMap<String, Object> map, List<ValueSet> wrapperLst) {wrapperLst.add(new ValueSet(counter, map));}

	 private List<ValueSet> generateAndReturnMap(long counter, String keyType, String key, Object value,
				List<ValueSet> wrapperLst) {
			HashMap<String, Object> map = new HashMap<>();
			map.put(key, value);
			switch (keyType) {
			case TEXT_NODE:
				populateRecordSet(counter, map, wrapperLst);
				break;
			case INT_NODE:
				populateRecordSet(counter, map, wrapperLst);
				break;
			case DOUBLE_NODE:
				populateRecordSet(counter, map, wrapperLst);
				break;
			case LONG_NODE:
				populateRecordSet(counter, map, wrapperLst);
				break;
			case BOOLEAN_NODE:
				populateRecordSet(counter, map, wrapperLst);
				break;
			default:
				break;
			}

			return wrapperLst;
		}

	 private void invoke(Entry<String, JsonNode> e,List<ValueSet> boollst) {
			if (checkchildObjectsState.apply(e.getValue())) {
				try {
					counter[0]++;
					searchJsonSchema(e.getKey().toString(), e.getValue().toString(), counter, intLst,
							dblLst, lngLst, strLst, boollst);
				} catch (JsonProcessingException e1) {
					e1.printStackTrace();
				}
			} else {
				// System.out.println(e.getKey());
			}
		}

		private void parse(String fragmentCandidate, int[] counter, List<ValueSet> boollst) {
			try {
				mapper.readTree(fragmentCandidate).fields().forEachRemaining(e -> {
					invoke(e, boollst);
				});
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}

		private void searchJsonSchema(String keys, String fragmentCandidate, int[] counter, List<ValueSet> intlst,
				List<ValueSet> dbllst, List<ValueSet> lnglst, List<ValueSet> strlst, List<ValueSet> boollst)
				throws JsonMappingException, JsonProcessingException {
			/*
			 * you may optionally apply logger System.out.println(fragmentCandidate); to
			 * observe how each time the json child node is fetched until it reaches
			 * abosolute root while spanning horizontally and until it reaches the last key.
			 */
			if (counter[0] >= 1) {
				// counter maintained to skip processing the JSON object initially passed,
				// intention is to read child objects, increment is done via recursion
				// programming method.
				var node = mapper.readTree(fragmentCandidate);
				var cls = readValueFromNode(node);

				switch (cls) {
				case ARRAY_NODE:
					if (node.size() >= 1) {
						node.forEach(e -> {
							var typeToMatch = readValueFromNode(e);
							if (null != e.toPrettyString()
									&& (typeToMatch.equals("ObjectNode") || supportedTypes.contains(typeToMatch))) {
								// calling the same declared function again with new small
								// json String fragment. counter subsequently incremented.
								try {
									searchJsonSchema(null, e.toPrettyString(), counter, intlst, dbllst, lnglst, strlst,
											boollst);
								} catch (JsonProcessingException e1) {
									e1.printStackTrace();
								}
							}
						});
					}
					break;
				case TEXT_NODE:
					generateAndReturnMap(++txtNodeConter, TEXT_NODE, returnKeyAppendedWithLstFlag(keys, TEXT_NODE),
							node.textValue(), strlst);
					break;
				case INT_NODE:
					generateAndReturnMap(++intNodeCounter, INT_NODE, returnKeyAppendedWithLstFlag(keys, INT_NODE),
							node.intValue(), intlst);
					break;
				case DOUBLE_NODE:
					generateAndReturnMap(++dblNodeCounter, DOUBLE_NODE, returnKeyAppendedWithLstFlag(keys, DOUBLE_NODE),
							node.doubleValue(), dbllst);
					break;
				case LONG_NODE:
					generateAndReturnMap(++lngNodeCounter, LONG_NODE, returnKeyAppendedWithLstFlag(keys, LONG_NODE),
							node.longValue(), lnglst);
					break;
				case BOOLEAN_NODE:
					generateAndReturnMap(++booleanNodeCounter, BOOLEAN_NODE,
							returnKeyAppendedWithLstFlag(keys, BOOLEAN_NODE), node.booleanValue(), boollst);
					break;
				default:
					break;
				}
			}
			// invoked when counter=0

			if (isKeyArray.test(mapper, fragmentCandidate)) {
				for (JsonNode node : mapper.readTree(fragmentCandidate)) {
					mapper.readTree(node.toString()).fields().forEachRemaining(e -> {
						invoke(e,boollst);
					});
				}
			} else {
				parse(fragmentCandidate, counter, boollst);
			}

		}
		

		
	class ServiceComponent{ 
		ServiceComponent(String payload) {
		
			try {
				searchJsonSchema(null, payload, counter, intLst, dblLst, lngLst, strLst, booleanLst);
				map.put(DataTypes.INT.name(), streamToList(intLst.stream()));
				map.put(DataTypes.LONG.name(), streamToList(lngLst.stream()));
				map.put(DataTypes.STRING.name(), streamToList(strLst.stream()));
				map.put(DataTypes.DOUBLE.name(), streamToList(dblLst.stream()));
				map.put(DataTypes.BOOLEAN.name(), streamToList(booleanLst.stream()));
				System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map));
			} catch (Exception e) {
				e.printStackTrace();
			} finally { 
				txtNodeConter = -1;
				intNodeCounter = -1;
				dblNodeCounter = -1;
				lngNodeCounter = -1;
				booleanNodeCounter = -1;
				
			}
		}

		private void clearContainers() {
			intLst.clear();
			lngLst.clear();
			strLst.clear();
			dblLst.clear();
			booleanLst.clear();
			map.clear();
		}

		public String get(int index) {
			if (!((List<?>) map.get(DataTypes.STRING.name())).isEmpty()) { 
				if (index >= ((List<?>) map.get(DataTypes.STRING.name())).size()) {
					return null;
				} else {
					return Optional.ofNullable(((List<?>) map.get(DataTypes.STRING.name())).get(index)).map(String::valueOf)
							.orElse(null);
				}
			} else {
				return null;
			}
			
		}
		 
	}
	
	class Delegate {
		public Delegate() {} 
		public void test(String payload) {
			ServiceComponent srvComponent=  new ServiceComponent(payload);
			System.out.println(srvComponent.get(10));
		}
	}
}
