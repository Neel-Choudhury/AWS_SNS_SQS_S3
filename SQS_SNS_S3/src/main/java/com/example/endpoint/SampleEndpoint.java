package com.example.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleEndpoint {

	@Autowired
	ParserEngine parserEngine;

	@PostMapping("/testMapping")
	public String getSamokeResponse(@RequestBody String payload) {
		parserEngine.new Delegate().test(payload);
		return payload;
	}
}
/*
 * interface CommonUtils { String CLASS_PKG_NAME =
 * "class com.fasterxml.jackson.databind.node."; String ARRAY_NODE =
 * "ArrayNode"; String TEXT_NODE = "TextNode"; String INT_NODE = "IntNode";
 * String DOUBLE_NODE = "DoubleNode"; String LONG_NODE = "LongNode"; String
 * BOOLEAN_NODE = "BooleanNode"; List<String> supportedTypes =
 * List.of(ARRAY_NODE, TEXT_NODE, INT_NODE, DOUBLE_NODE, LONG_NODE,
 * BOOLEAN_NODE); Function<JsonNode, Boolean> checkchildObjectsState = (var
 * childNodeCanidate) -> { return !childNodeCanidate.isNull() ||
 * !childNodeCanidate.isEmpty(); }; Function<Stream<?>, List<?>>
 * convertStreamToList = (var stream) -> { return
 * stream.collect(Collectors.toList()); };
 * 
 * default List<?> streamToList(Stream<?> stream) { return
 * convertStreamToList.apply(stream.distinct()); };
 * 
 * default String returnKeyAppendedWithLstFlag(String keys, String nodeType) {
 * return Optional.ofNullable(keys).map(String::valueOf).orElse("LIST_" +
 * nodeType); }
 * 
 * BiPredicate<ObjectMapper, String> isKeyArray = (mapper, content) -> { try {
 * return mapper.readTree(content).isArray(); } catch (Exception e) {
 * e.printStackTrace(); return false; } };
 * 
 * default String readValueFromNode(JsonNode node) { return
 * node.getClass().toString().replace(CLASS_PKG_NAME, "").trim(); }
 * 
 * default void populateRecordSet(long counter, HashMap<String, Object> map,
 * List<ValueSet> wrapperLst) { wrapperLst.add(new ValueSet(counter, map)); }
 * 
 * default List<ValueSet> generateAndReturnMap(long counter, String keyType,
 * String key, Object value, List<ValueSet> wrapperLst) { HashMap<String,
 * Object> map = new HashMap<>(); map.put(key, value); switch (keyType) { case
 * TEXT_NODE: populateRecordSet(counter, map, wrapperLst); break; case INT_NODE:
 * populateRecordSet(counter, map, wrapperLst); break; case DOUBLE_NODE:
 * populateRecordSet(counter, map, wrapperLst); break; case LONG_NODE:
 * populateRecordSet(counter, map, wrapperLst); break; case BOOLEAN_NODE:
 * populateRecordSet(counter, map, wrapperLst); break; default: break; }
 * 
 * return wrapperLst; }
 * 
 * Supplier<List<ValueSet>> getGenericObjectWrapper = () -> { return new
 * ArrayList<>(); }; }
 * 
 * @Component class PayloadProcessor implements CommonUtils {
 * 
 * @Autowired ObjectMapper mapper;
 * 
 * long txtNodeConter = -1L; long intNodeCounter = -1L; long dblNodeCounter =
 * -1L; long lngNodeCounter = -1L; long booleanNodeCounter = -1L;
 * 
 * @SuppressWarnings("deprecation") public void test(String payload) { try {
 * HashMap<String, Object> map = new LinkedHashMap<>(); List<ValueSet> intlst =
 * getGenericObjectWrapper.get(); List<ValueSet> dbllst =
 * getGenericObjectWrapper.get(); List<ValueSet> lnglst =
 * getGenericObjectWrapper.get(); List<ValueSet> booleanlst =
 * getGenericObjectWrapper.get(); List<ValueSet> strlst =
 * getGenericObjectWrapper.get();
 * mapper.setVisibilityChecker(mapper.getVisibilityChecker().withFieldVisibility
 * (Visibility.ANY)
 * .withGetterVisibility(Visibility.NONE).withIsGetterVisibility(Visibility.NONE
 * )); int[] counter = { 0 }; searchJsonSchema(null, payload, counter, intlst,
 * dbllst, lnglst, strlst, booleanlst); map.put("int",
 * streamToList(intlst.stream())); map.put("long",
 * streamToList(lnglst.stream())); map.put("string",
 * streamToList(strlst.stream())); map.put("double",
 * streamToList(dbllst.stream())); map.put("boolean",
 * streamToList(booleanlst.stream())); } catch (Exception e) {
 * e.printStackTrace(); } finally { txtNodeConter = -1L; intNodeCounter = -1L;
 * dblNodeCounter = -1L; lngNodeCounter = -1L; booleanNodeCounter = -1L; }
 * 
 * }
 * 
 * private void searchJsonSchema(String keys, String fragmentCandidate, int[]
 * counter, List<ValueSet> intlst, List<ValueSet> dbllst, List<ValueSet> lnglst,
 * List<ValueSet> strlst, List<ValueSet> boollst) throws JsonMappingException,
 * JsonProcessingException { //you may optionally apply logger
 * System.out.println(fragmentCandidate); to //observe how each time the json
 * child node is fetched until it reaches //abosolute root while spanning
 * horizontally and until it reaches the last key. if (counter[0] >= 1) { //
 * counter maintained to skip processing the JSON object initially passed, //
 * intention is to read child objects, increment is done via recursion //
 * programming method. var node = mapper.readTree(fragmentCandidate); var cls =
 * readValueFromNode(node);
 * 
 * switch (cls) { case ARRAY_NODE: if (node.size() >= 1) { node.forEach(e -> {
 * var typeToMatch = readValueFromNode(e); if (null != e.toPrettyString() &&
 * (typeToMatch.equals("ObjectNode") || supportedTypes.contains(typeToMatch))) {
 * // calling the same declared function again with new small // json String
 * fragment. counter subsequently incremented. try { searchJsonSchema(null,
 * e.toPrettyString(), counter, intlst, dbllst, lnglst, strlst, boollst); }
 * catch (JsonProcessingException e1) { e1.printStackTrace(); } } }); } break;
 * case TEXT_NODE: generateAndReturnMap(++txtNodeConter, TEXT_NODE,
 * returnKeyAppendedWithLstFlag(keys, TEXT_NODE), node.textValue(), strlst);
 * break; case INT_NODE: generateAndReturnMap(++intNodeCounter, INT_NODE,
 * returnKeyAppendedWithLstFlag(keys, INT_NODE), node.intValue(), intlst);
 * break; case DOUBLE_NODE: generateAndReturnMap(++dblNodeCounter, DOUBLE_NODE,
 * returnKeyAppendedWithLstFlag(keys, DOUBLE_NODE), node.doubleValue(), dbllst);
 * break; case LONG_NODE: generateAndReturnMap(++lngNodeCounter, LONG_NODE,
 * returnKeyAppendedWithLstFlag(keys, LONG_NODE), node.longValue(), lnglst);
 * break; case BOOLEAN_NODE: generateAndReturnMap(++booleanNodeCounter,
 * BOOLEAN_NODE, returnKeyAppendedWithLstFlag(keys, BOOLEAN_NODE),
 * node.booleanValue(), boollst); break; default: break; } } // invoked when
 * counter=0
 * 
 * if (isKeyArray.test(mapper, fragmentCandidate)) { for (JsonNode node :
 * mapper.readTree(fragmentCandidate)) {
 * mapper.readTree(node.toString()).fields().forEachRemaining(e -> { if
 * (checkchildObjectsState.apply(e.getValue())) { try { counter[0]++;
 * searchJsonSchema(e.getKey(), e.getValue().toString(), counter, intlst,
 * dbllst, lnglst, strlst, boollst); } catch (JsonProcessingException e1) {
 * e1.printStackTrace(); } } else { //System.out.println(e.getKey()); } }); } }
 * else { parse(fragmentCandidate, counter,
 * boollst,intlst,dbllst,lnglst,strlst); }
 * 
 * }
 * 
 * private void parse(String fragmentCandidate, int[] counter, List<ValueSet>
 * boollst, List<ValueSet> intlst, List<ValueSet> dbllst, List<ValueSet> lnglst,
 * List<ValueSet> strlst) { try {
 * mapper.readTree(fragmentCandidate).fields().forEachRemaining(e -> { if
 * (checkchildObjectsState.apply(e.getValue())) { try { counter[0]++;
 * searchJsonSchema(e.getKey().toString(), e.getValue().toString(), counter,
 * intlst, dbllst, lnglst, strlst, boollst); } catch (JsonProcessingException
 * e1) { e1.printStackTrace(); } } else { //System.out.println(e.getKey()); }
 * });
 * 
 * 
 * 
 * } catch (JsonProcessingException e) { e.printStackTrace(); } }
 * 
 * }
 */