package org.withor.voidline.api;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class TabList {
    @Getter private String header;
    @Getter private String footer;

    private TabList(Builder builder) {
        this.header = builder.header;
        this.footer = builder.footer;
    }

    public static class Builder {
        private String header;
        private String footer;

        public Builder setHeader(String header) {
            this.header = header;
            return this;
        }

        public Builder setFooter(String footer) {
            this.footer = footer;
            return this;
        }

        public TabList build() {
            return new TabList(this);
        }
    }
}