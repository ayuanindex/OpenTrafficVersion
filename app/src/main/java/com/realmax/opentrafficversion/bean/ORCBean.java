package com.realmax.opentrafficversion.bean;

import java.util.List;

/**
 * @ProjectName: BaiduApiTest
 * @Package: com.realmax.baiduapitest.bean
 * @ClassName: ORCBean
 * @CreateDate: 2020/3/19 14:48
 */
public class ORCBean {
    /**
     * log_id : 1857101796895030515
     * words_result : {"color":"blue","number":"鲁B660HB","probability":[0.9013630151748657,0.8985164761543274,0.9017070531845093,0.9023152589797974,0.9018282294273376,0.9004107117652893,0.8998597860336304],"vertexes_location":[{"y":610,"x":943},{"y":626,"x":1646},{"y":822,"x":1643},{"y":804,"x":941}]}
     */

    private long log_id;
    private WordsResultBean words_result;

    public long getLog_id() {
        return log_id;
    }

    public void setLog_id(long log_id) {
        this.log_id = log_id;
    }

    public WordsResultBean getWords_result() {
        return words_result;
    }

    public void setWords_result(WordsResultBean words_result) {
        this.words_result = words_result;
    }

    public static class WordsResultBean {
        /**
         * color : blue
         * number : 鲁B660HB
         * probability : [0.9013630151748657,0.8985164761543274,0.9017070531845093,0.9023152589797974,0.9018282294273376,0.9004107117652893,0.8998597860336304]
         * vertexes_location : [{"y":610,"x":943},{"y":626,"x":1646},{"y":822,"x":1643},{"y":804,"x":941}]
         */

        private String color;
        private String number;
        private List<Double> probability;
        private List<VertexesLocationBean> vertexes_location;

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public List<Double> getProbability() {
            return probability;
        }

        public void setProbability(List<Double> probability) {
            this.probability = probability;
        }

        public List<VertexesLocationBean> getVertexes_location() {
            return vertexes_location;
        }

        public void setVertexes_location(List<VertexesLocationBean> vertexes_location) {
            this.vertexes_location = vertexes_location;
        }

        public static class VertexesLocationBean {
            /**
             * y : 610
             * x : 943
             */

            private int y;
            private int x;

            public int getY() {
                return y;
            }

            public void setY(int y) {
                this.y = y;
            }

            public int getX() {
                return x;
            }

            public void setX(int x) {
                this.x = x;
            }

            @Override
            public String toString() {
                return "VertexesLocationBean{" +
                        "y=" + y +
                        ", x=" + x +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "WordsResultBean{" +
                    "color='" + color + '\'' +
                    ", number='" + number + '\'' +
                    ", probability=" + probability +
                    ", vertexes_location=" + vertexes_location +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ORCBean{" +
                "log_id=" + log_id +
                ", words_result=" + words_result +
                '}';
    }
}
