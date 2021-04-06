package ru.filippov.neatexecutor.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neat4j.neat.data.core.NetworkInput;
import org.neat4j.neat.data.core.NetworkOutput;
import org.neat4j.neat.data.set.ExpectedOutputImpl;
import org.neat4j.neat.data.set.InputImpl;
import ru.filippov.neatexecutor.exception.IncorrectFileFormatException;

import java.io.*;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectConfig {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NormalizedDataDto implements Serializable {

        private List<ColumnsDto> columns;

        private Integer trainEndIndex;

        private Integer testEndIndex;

        public NormalizedDataDto(byte[] bytes, List<ColumnsDto> columns, int trainEndIndex, int testEndIndex) throws IOException, IncorrectFileFormatException {
            this.trainEndIndex = trainEndIndex;
            this.testEndIndex = testEndIndex;
            this.columns = columns;
            if (bytes!=null){
                this.parseByte(bytes, columns);
            }


        }

        private void parseByte(byte[] data, List<ColumnsDto> columns) throws IOException, IncorrectFileFormatException {
            final BufferedReader brl = new BufferedReader(
                    new InputStreamReader(new ByteArrayInputStream(data)));
            String line = brl.readLine();

            if(line == null) {
                throw new IncorrectFileFormatException("First line of the file must contain headers!");
            }

            StringTokenizer stringTokenizer = new StringTokenizer(line, ";");
            int tokenIndex = 0;
            int lineIndex = 0;

            Map<String, ColumnsDto> dataMap = new HashMap<>(columns.size());

            String[] columnsNames = new String[columns.size()];

            for (ColumnsDto columnsDto : columns) {
                dataMap.put(columnsDto.getColumnName(), columnsDto);
                if(columnsDto.getData() == null){
                    columnsDto.setData(new ArrayList<>(1000));
                }
            }


            while (stringTokenizer.hasMoreTokens()) {
                final String header = stringTokenizer.nextToken();
                if(!dataMap.containsKey(header)){
                    throw new IOException("File and Columns names are mismatched!");
                }
                columnsNames[tokenIndex] = header;
                tokenIndex++;
            }

            lineIndex++;
            while ((line = brl.readLine()) != null) {

                stringTokenizer = new StringTokenizer(line, ";");
                tokenIndex = -1;

                while (stringTokenizer.hasMoreTokens()) {
                    try {
                        tokenIndex++;
                        final String columnName = columnsNames[tokenIndex];
                        final Double value = Double.parseDouble(stringTokenizer.nextToken());
                        dataMap.get(columnName).getData().add(value);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        throw new IncorrectFileFormatException("Cant read value at "+ lineIndex+1 + " " + tokenIndex+1  +" as double");
                    }
                }
                lineIndex++;
            }

            brl.close();

        }
    }

    
    private NormalizedDataDto normalizedData;

    private List<Map<String, Object>> settings;

    private Short windowSize;

    private Short predictionPeriod;




}
