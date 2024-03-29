package org.fisco.solc.compiler;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompilationResult {

    private static final Logger logger = LoggerFactory.getLogger(CompilationResult.class);

    private Map<String, ContractMetadata> contracts = Collections.emptyMap();

    public String version = "";

    public static CompilationResult parse(String rawJson) throws IOException {
        logger.debug("rawJson: {}", rawJson);
        if (rawJson == null || rawJson.isEmpty()) {
            CompilationResult empty = new CompilationResult();
            empty.contracts = Collections.emptyMap();
            empty.version = "";

            return empty;
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            JsonObject jsonObject =
                    com.google.gson.JsonParser.parseString(rawJson).getAsJsonObject();
            JsonObject result = new JsonObject();
            JsonObject asJsonObject = jsonObject.get("contracts").getAsJsonObject();
            Set<String> contractNameList = asJsonObject.keySet();
            Object[] contractName = contractNameList.toArray();
            JsonObject contractObject = new JsonObject();
            for (Object contract : contractName) {
                JsonObject contractJsonObject =
                        asJsonObject.get(contract.toString()).getAsJsonObject();
                JsonObject abiObject = new JsonObject();
                abiObject.addProperty("abi", contractJsonObject.get("abi").toString());
                abiObject.addProperty("bin", contractJsonObject.get("bin").getAsString());
                abiObject.addProperty("metadata", contractJsonObject.get("metadata").getAsString());

                if (contractJsonObject.get("userdoc") != null) {
                    abiObject.addProperty("userdoc", contractJsonObject.get("userdoc").toString());
                }

                if (contractJsonObject.get("devdoc") != null) {
                    abiObject.addProperty("devdoc", contractJsonObject.get("devdoc").toString());
                }
                contractObject.add(contract.toString(), abiObject);
            }
            result.add("contracts", contractObject);
            result.addProperty("version", jsonObject.get("version").getAsString());
            return objectMapper.readValue(result.toString(), CompilationResult.class);
        }
    }

    /**
     * @param contractName The contract name
     * @return the first contract found for a given contract name; use {@link #getContract(Path,
     *     String)} if this compilation result contains more than one contract with the same name
     */
    public ContractMetadata getContract(String contractName) {

        for (Map.Entry<String, ContractMetadata> entry : contracts.entrySet()) {
            String key = entry.getKey();
            String name = key.substring(key.lastIndexOf(':') + 1);
            if (contractName.equals(name)) {
                return entry.getValue();
            }
        }
        throw new UnsupportedOperationException(
                "No contract found with name '"
                        + contractName
                        + "'. Please specify a valid contract name. Available keys ("
                        + getContractKeys()
                        + ").");
    }

    /**
     * @param contractPath The contract path
     * @param contractName The contract name
     * @return the contract with key {@code contractPath:contractName} if it exists; {@code null}
     *     otherwise
     */
    public ContractMetadata getContract(Path contractPath, String contractName) {
        return contracts.get(contractPath.toAbsolutePath().toString() + ':' + contractName);
    }

    /** @return all contracts from this compilation result */
    public List<ContractMetadata> getContracts() {
        return new ArrayList<>(contracts.values());
    }

    /** @return all keys from this compilation result */
    public List<String> getContractKeys() {
        return new ArrayList<>(contracts.keySet());
    }

    public static class ContractMetadata {
        public String abi;
        public String bin;
        public String metadata;
        public String userdoc;
        public String devdoc;

        @Override
        public String toString() {
            return "ContractMetadata [abi="
                    + abi
                    + ", bin="
                    + bin
                    + ", metadata="
                    + metadata
                    + ", userdoc="
                    + userdoc
                    + ", devdoc="
                    + devdoc
                    + "]";
        }
    }
}
