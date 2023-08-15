import click
import sys

def gen_boxes(input_file):
    f = open(input_file)

    boxes = []
    digit_idx = 0
    box = 0
    half_byte = 0
    mask = 1
    for line in f:
        for ch in line.strip():
            if ch != "1" and ch != "0":
                continue

            if ch == "1":
                half_byte |= mask

            digit_idx += 1
            mask <<= 1

            if digit_idx % 4 == 0:
                box = (box << 4) | half_byte
                half_byte = 0
                mask = 1

            if digit_idx == 16:
                boxes.append(box)
                digit_idx = 0
                box = 0

    f.close()

    return boxes

def write_boxes(output_file, boxes):
    f = open(output_file, "w") if output_file != "" else sys.stdout

    for i in range(len(boxes)):
        f.write("{0:x}".format(boxes[i]))
        f.write(" " if (i + 1) % 4 > 0 else "\n")

    if f != sys.stdout:
        f.close()

@click.command()
@click.option("-i", "--input-file", type=str, required=True, help="Input file path")
@click.option("-o", "--output-file", type=str, default="", help="Output file path")
def main(input_file, output_file):
    boxes = gen_boxes(input_file)
    write_boxes(output_file, boxes)

if __name__ == "__main__":
    main()
