import argparse
import gzip
import pandas as pd
from tqdm import tqdm  # Import tqdm for progress bar
from google.protobuf.message import Message
from protobuf_schema_pb2 import BackupData, BackupBarcode, BackupColor


def excel_to_protobuf(barcodes_excel: str, colors_excel: str, output_file: str):
    backup_data = BackupData()

    # Count products
    product_count = 0
    # Skip the first 5 rows to start reading from row 6
    barcodes_df = pd.read_excel(barcodes_excel, skiprows=4)

    # Create a new DataFrame to store filtered data
    filtered_barcodes = []

    # Use tqdm to show progress for the barcodes DataFrame
    for index, row in tqdm(barcodes_df.iterrows(), total=barcodes_df.shape[0], desc="Processing Barcodes & Removing Duplicates"):
        # Split the SKU, Colour, and Size from Column A
        sku_color_size = str(row[barcodes_df.columns[0]]).strip().split('/')  # Access the first column dynamically
        if len(sku_color_size) >= 3:
            sku = sku_color_size[0].strip()
            color = sku_color_size[1].strip()
            size = '/'.join(sku_color_size[2:]).strip()  # Join remaining parts as size if there are more than 2 slashes
        else:
            continue  # Skip this row if it doesn't have enough parts

        # Append the filtered data to the list
        filtered_barcodes.append({
            'sku': sku,
            'color': color,
            'size': size,
            'name': str(row[barcodes_df.columns[2]]).strip(),  # Product Name from Column C (third column)
            'piece_barcode': str(int(float(row[barcodes_df.columns[6]]))) if pd.notna(row[barcodes_df.columns[6]]) else "",
            'gtin': str(int(float(row[barcodes_df.columns[8]]))) if pd.notna(row[barcodes_df.columns[8]]) else ""

        })

    # Create a DataFrame from the filtered data
    filtered_barcodes_df = pd.DataFrame(filtered_barcodes)

    # Remove duplicates
    filtered_barcodes_df = filtered_barcodes_df.drop_duplicates(
        subset=['sku', 'color', 'size', 'name', 'piece_barcode', 'gtin'])

    # Ask user if they want to filter SKUs that don’t start with A, B, or a number
    user_input = input("Would you like to remove all products that don't start with A, B, or a Number? (yes/no): ").strip().lower()
    if user_input in ['yes', 'y']:
        filtered_barcodes_df = filtered_barcodes_df[filtered_barcodes_df['sku'].str.match(r'^[AB0-9]', na=False)]

    # Use tqdm to show progress for adding data to Protobuf
    for index, row in tqdm(filtered_barcodes_df.iterrows(), total=filtered_barcodes_df.shape[0],
                           desc="Adding Products to Protobuf"):
        barcode = backup_data.barcodes.add()
        barcode.sku = row['sku']
        barcode.color = row['color']
        barcode.size = row['size']
        barcode.name = row['name']
        barcode.piece_barcode = row['piece_barcode']
        barcode.gtin = row['gtin']
        product_count += 1

    # Count colors
    color_count = 0
    colors_df = pd.read_excel(colors_excel)

    for index, row in tqdm(colors_df.iterrows(), total=colors_df.shape[0], desc="Adding Colors to Protobuf"):
        # Access color code and color name dynamically
        color = backup_data.colors.add()
        color.color_code = str(row[colors_df.columns[0]]).strip()  # Adjust index based on actual column
        color.color_name = str(row[colors_df.columns[1]]).strip()  # Adjust index based on actual column
        color_count += 1

    # Save to compressed Protobuf file
    with gzip.open(output_file + ".gz", "wb") as f:
        f.write(backup_data.SerializeToString())

    print(f"✅ Protobuf file saved as {output_file}.gz")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Convert Excel to Protobuf")
    parser.add_argument("--barcodes", required=True, help="Path to barcodes Excel file")
    parser.add_argument("--colors", required=True, help="Path to colors Excel file")
    parser.add_argument("--output", default="products.proto", help="Output Protobuf file")

    args = parser.parse_args()
    excel_to_protobuf(args.barcodes, args.colors, args.output)